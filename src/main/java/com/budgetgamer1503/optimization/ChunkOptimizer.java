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
    
    // Track player positions and view directions
    private static final Map<UUID, PlayerChunkData> playerChunkData = new HashMap<>();
    
    // Cache for frequently accessed chunks - increased size to prevent thrashing
    private static final LinkedHashMap<ChunkPos, Long> chunkCache = new LinkedHashMap<>(256, 0.75f, true);
    private static final int MAX_CACHE_SIZE = 500;
    private static final long CACHE_EXPIRY_MS = 30000; // 30 seconds
    
    // Throttle chunk unload operations to prevent batch reloads
    private static long lastUnloadCheckTime = 0;
    private static final long UNLOAD_CHECK_COOLDOWN_MS = 2000; // 2 seconds between unload checks
    private static int unloadBatchCount = 0;
    private static final int MAX_UNLOADS_PER_BATCH = 16; // Max chunks to unload per batch
    
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
        OptidumConfig config = ConfigManager.getConfig();
        if (!config.chunkLoadingOptimization) {
            return;
        }
        
        BlockPos pos = player.blockPosition();
        ChunkPos chunkPos = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
        float yaw = player.getYRot();
        float pitch = player.getXRot();
        
        playerChunkData.put(player.getUUID(), new PlayerChunkData(chunkPos, yaw, pitch));
        
        // Update chunk cache with timestamp
        updateChunkCache(chunkPos);
    }
    
    private static void updateChunkCache(ChunkPos chunkPos) {
        chunkCache.put(chunkPos, System.currentTimeMillis());
        
        // Evict expired entries instead of just oldest
        if (chunkCache.size() > MAX_CACHE_SIZE) {
            evictExpiredEntries();
        }
        
        // If still over limit, evict oldest (LRU via access-order LinkedHashMap)
        if (chunkCache.size() > MAX_CACHE_SIZE) {
            Iterator<ChunkPos> iterator = chunkCache.keySet().iterator();
            int toRemove = chunkCache.size() - MAX_CACHE_SIZE;
            for (int i = 0; i < toRemove && iterator.hasNext(); i++) {
                iterator.next();
                iterator.remove();
            }
        }
    }
    
    private static void evictExpiredEntries() {
        long now = System.currentTimeMillis();
        chunkCache.entrySet().removeIf(entry -> (now - entry.getValue()) > CACHE_EXPIRY_MS);
    }
    
    public static boolean shouldLoadChunk(ChunkPos chunkPos) {
        OptidumConfig config = ConfigManager.getConfig();
        if (!config.chunkLoadingOptimization) {
            return true; // Load all chunks if optimization is disabled
        }
        
        // Check if chunk is in cache (recently accessed)
        if (chunkCache.containsKey(chunkPos)) {
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
        OptidumConfig config = ConfigManager.getConfig();
        if (!config.chunkLoadingOptimization || !config.unloadDistantChunks) {
            return false; // Use default unloading
        }
        
        // Throttle unload operations to prevent batch reload cascades
        long now = System.currentTimeMillis();
        if (now - lastUnloadCheckTime < UNLOAD_CHECK_COOLDOWN_MS) {
            return false;
        }
        
        // Reset batch counter if cooldown has passed
        if (now - lastUnloadCheckTime >= UNLOAD_CHECK_COOLDOWN_MS) {
            unloadBatchCount = 0;
            lastUnloadCheckTime = now;
        }
        
        // Limit unloads per batch
        if (unloadBatchCount >= MAX_UNLOADS_PER_BATCH) {
            return false;
        }
        
        // Check if chunk is in cache
        if (chunkCache.containsKey(chunkPos)) {
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
        
        unloadBatchCount++;
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
        OptidumConfig config = ConfigManager.getConfig();
        if (config.enableDebugLogging) {
            LOGGER.debug("Chunk optimizer stats: {} players, {} cached chunks", 
                playerChunkData.size(), chunkCache.size());
        }
    }
}