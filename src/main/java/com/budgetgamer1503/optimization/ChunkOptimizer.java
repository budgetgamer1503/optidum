package com.budgetgamer1503.optimization;

import com.budgetgamer1503.Optidum;
import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ChunkOptimizer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/ChunkOptimizer");
    private static final OptidumConfig config = ConfigManager.getConfig();
    
    // Track player positions and view directions
    private static final Map<UUID, PlayerChunkData> playerChunkData = new HashMap<>();
    
    // Cache for frequently accessed chunks
    private static final Set<ChunkPos> chunkCache = new LinkedHashSet<>();
    private static final int MAX_CACHE_SIZE = 100;
    
    public static class PlayerChunkData {
        public ChunkPos currentChunk;
        public float yaw;
        public float pitch;
        public long lastUpdate;
        
        public PlayerChunkData(ChunkPos chunk, float yaw, float pitch) {
            this.currentChunk = chunk;
            this.yaw = yaw;
            this.pitch = pitch;
            this.lastUpdate = System.currentTimeMillis();
        }
    }
    
    public static void updatePlayerPosition(ServerPlayer player) {
        if (!config.chunkLoadingOptimization) {
            return;
        }
        
        BlockPos pos = player.blockPosition();
        // ChunkPos constructor takes x and z coordinates separately
        ChunkPos chunkPos = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
        float yaw = player.getYRot();
        float pitch = player.getXRot();
        
        playerChunkData.put(player.getUUID(), new PlayerChunkData(chunkPos, yaw, pitch));
        
        // Update chunk cache
        updateChunkCache(chunkPos);
    }
    
    private static void updateChunkCache(ChunkPos chunkPos) {
        chunkCache.add(chunkPos);
        
        // Limit cache size
        if (chunkCache.size() > MAX_CACHE_SIZE) {
            Iterator<ChunkPos> iterator = chunkCache.iterator();
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }
    }
    
    public static boolean shouldLoadChunk(ChunkPos chunkPos) {
        if (!config.chunkLoadingOptimization) {
            return true; // Load all chunks if optimization is disabled
        }
        
        // Check if chunk is in cache
        if (chunkCache.contains(chunkPos)) {
            return true;
        }
        
        // Check distance to players
        int loadDistance = config.smartChunkLoadDistance;
        
        for (PlayerChunkData playerData : playerChunkData.values()) {
            int dx = Math.abs(chunkPos.x() - playerData.currentChunk.x());
            int dz = Math.abs(chunkPos.z() - playerData.currentChunk.z());
            
            if (dx <= loadDistance && dz <= loadDistance) {
                // Check view direction if enabled
                if (config.prioritizeViewDirection) {
                    if (isInViewDirection(chunkPos, playerData)) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private static boolean isInViewDirection(ChunkPos chunkPos, PlayerChunkData playerData) {
        // Simple view direction check based on yaw
        // In a real implementation, this would be more sophisticated
        // Use record accessor methods for chunk coordinates
        int chunkX = chunkPos.x();
        int chunkZ = chunkPos.z();
        int playerChunkX = playerData.currentChunk.x();
        int playerChunkZ = playerData.currentChunk.z();
        
        double angleToChunk = Math.toDegrees(Math.atan2(
            chunkZ - playerChunkZ,
            chunkX - playerChunkX
        ));
        
        double angleDiff = Math.abs(normalizeAngle(angleToChunk - playerData.yaw));
        return angleDiff < 90; // Within 90 degrees of view direction
    }
    
    private static double normalizeAngle(double angle) {
        angle %= 360;
        if (angle > 180) {
            angle -= 360;
        } else if (angle < -180) {
            angle += 360;
        }
        return angle;
    }
    
    public static boolean shouldUnloadChunk(ChunkPos chunkPos) {
        if (!config.chunkLoadingOptimization || !config.unloadDistantChunks) {
            return false; // Use default unloading
        }
        
        // Check if chunk is in cache
        if (chunkCache.contains(chunkPos)) {
            return false;
        }
        
        // Check distance to players with a larger threshold
        int unloadDistance = config.smartChunkLoadDistance + 4;
        
        for (PlayerChunkData playerData : playerChunkData.values()) {
            int dx = Math.abs(chunkPos.x() - playerData.currentChunk.x());
            int dz = Math.abs(chunkPos.z() - playerData.currentChunk.z());
            
            if (dx <= unloadDistance && dz <= unloadDistance) {
                return false; // Keep chunk loaded
            }
        }
        
        return true; // Unload distant chunk
    }
    
    public static void clearPlayerData(UUID playerId) {
        playerChunkData.remove(playerId);
    }
    
    public static void clearAllData() {
        playerChunkData.clear();
        chunkCache.clear();
    }
    
    public static void logStats() {
        if (config.enableDebugLogging) {
            LOGGER.debug("Chunk optimizer stats: {} players, {} cached chunks", 
                playerChunkData.size(), chunkCache.size());
        }
    }
}