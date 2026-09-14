package com.budgetgamer1503.optimization;

import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkOptimizer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/ChunkOptimizer");
    
    private static final Map<UUID, PlayerViewRecord> playerViewData = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> lastCrossingTimes = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> lastChunkPositions = new ConcurrentHashMap<>();

    public record PlayerViewRecord(int chunkX, int chunkZ, float yaw) {}

    public static void updatePlayer(ServerPlayer player) {
        OptidumConfig config = ConfigManager.getConfig();
        if (!config.chunkLoadingOptimization) {
            return;
        }

        int chunkX = player.getBlockX() >> 4;
        int chunkZ = player.getBlockZ() >> 4;
        float yaw = player.getYRot();
        UUID uuid = player.getUUID();

        PlayerViewRecord previous = playerViewData.get(uuid);
        if (previous == null || previous.chunkX != chunkX || previous.chunkZ != chunkZ || Math.abs(previous.yaw - yaw) > 10.0f) {
            playerViewData.put(uuid, new PlayerViewRecord(chunkX, chunkZ, yaw));

            if (previous != null && (previous.chunkX != chunkX || previous.chunkZ != chunkZ)) {
                checkBorderChurn(uuid, chunkX, chunkZ);
            }
        }
    }

    private static void checkBorderChurn(UUID uuid, int chunkX, int chunkZ) {
        long currentPos = ChunkPos.pack(chunkX, chunkZ);
        Long lastPos = lastChunkPositions.get(uuid);
        long now = System.currentTimeMillis();

        if (lastPos != null && lastPos == currentPos) {
            Long lastTime = lastCrossingTimes.get(uuid);
            if (lastTime != null && (now - lastTime) < 3000) {
                lastCrossingTimes.put(uuid, now);
                return;
            }
        }

        lastChunkPositions.put(uuid, currentPos);
        lastCrossingTimes.put(uuid, now);
    }

    public static boolean isChunkInViewDirection(ServerPlayer player, int chunkX, int chunkZ) {
        OptidumConfig config = ConfigManager.getConfig();
        if (!config.chunkLoadingOptimization || !config.prioritizeViewDirection) {
            return true;
        }

        PlayerViewRecord view = playerViewData.get(player.getUUID());
        if (view == null) {
            return true;
        }

        int dx = chunkX - view.chunkX;
        int dz = chunkZ - view.chunkZ;
        if (dx == 0 && dz == 0) {
            return true;
        }

        double angleToChunk = Math.toDegrees(Math.atan2(dz, dx));
        float playerYaw = (view.yaw % 360 + 360) % 360;
        double diff = Math.abs(normalizeAngle(angleToChunk - playerYaw));

        return diff <= 100.0;
    }

    private static double normalizeAngle(double angle) {
        angle = (angle + 180) % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle - 180;
    }

    public static void clearPlayer(UUID uuid) {
        playerViewData.remove(uuid);
        lastCrossingTimes.remove(uuid);
        lastChunkPositions.remove(uuid);
    }

    public static void tick() {
        if (playerViewData.size() > 256) {
            playerViewData.clear();
            lastCrossingTimes.clear();
            lastChunkPositions.clear();
        }
    }
}