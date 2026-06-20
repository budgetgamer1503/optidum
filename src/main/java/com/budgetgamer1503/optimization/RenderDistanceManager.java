package com.budgetgamer1503.optimization;

import com.budgetgamer1503.Optidum;
import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

public class RenderDistanceManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/RenderDistance");
    
    // FPS tracking
    private static final Queue<Long> frameTimes = new LinkedList<>();
    private static final int FPS_SAMPLE_COUNT = 60;
    private static long lastFrameTime = 0;
    
    // Synced from the user's current video setting so we do not force chunk reloads on startup.
    private static int currentRenderDistance = -1;
    private static int preferredRenderDistance = -1;
    private static int lastAppliedRenderDistance = -1;
    private static long lastAdjustmentTime = 0;
    private static final long ADJUSTMENT_COOLDOWN_MS = 30000; // 30 seconds
    
    // Smoothing
    private static final double SMOOTHING_FACTOR = 0.1;
    private static double smoothedFPS = 60.0;
    
    // Hysteresis: require consecutive same-direction signals before changing
    private static int consecutiveLowFpsCount = 0;
    private static int consecutiveHighFpsCount = 0;
    private static final int HYSTERESIS_THRESHOLD = 3; // Need 3 consecutive signals
    
    // Callback for applying render distance (set by client mixin)
    private static RenderDistanceApplier applier = null;
    
    @FunctionalInterface
    public interface RenderDistanceApplier {
        void apply(int renderDistance);
    }
    
    public static void setApplier(RenderDistanceApplier applier) {
        RenderDistanceManager.applier = applier;
    }
    
    public static void syncCurrentRenderDistance(int renderDistance) {
        if (renderDistance <= 0) {
            return;
        }
        
        if (currentRenderDistance < 0) {
            currentRenderDistance = renderDistance;
            preferredRenderDistance = renderDistance;
            return;
        }
        
        currentRenderDistance = renderDistance;
        if (renderDistance != lastAppliedRenderDistance) {
            preferredRenderDistance = renderDistance;
        }
    }
    
    public static void onFrame() {
        OptidumConfig config = ConfigManager.getConfig();
        if (!config.renderDistanceOptimization) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        
        // Calculate FPS
        if (lastFrameTime > 0) {
            long frameTime = currentTime - lastFrameTime;
            if (frameTime > 0) {
                double fps = 1000.0 / frameTime;
                updateFPSSample(fps);
            }
        }
        lastFrameTime = currentTime;
        
        // Adjust render distance if cooldown has passed
        if (currentTime - lastAdjustmentTime > ADJUSTMENT_COOLDOWN_MS) {
            adjustRenderDistance();
            lastAdjustmentTime = currentTime;
        }
    }
    
    private static void updateFPSSample(double fps) {
        frameTimes.add((long) fps);
        if (frameTimes.size() > FPS_SAMPLE_COUNT) {
            frameTimes.poll();
        }
        
        // Apply smoothing
        smoothedFPS = smoothedFPS * (1 - SMOOTHING_FACTOR) + fps * SMOOTHING_FACTOR;
    }
    
    public static double getAverageFPS() {
        if (frameTimes.isEmpty()) {
            return 60.0;
        }
        
        long sum = 0;
        for (Long fps : frameTimes) {
            sum += fps;
        }
        return (double) sum / frameTimes.size();
    }
    
    public static double getSmoothedFPS() {
        return smoothedFPS;
    }
    
    private static void adjustRenderDistance() {
        if (currentRenderDistance < 0) {
            return;
        }
        
        OptidumConfig config = ConfigManager.getConfig();
        double averageFPS = getAverageFPS();
        double targetFPS = config.targetFPS;
        int maxTargetRenderDistance = Math.min(config.maxRenderDistance, preferredRenderDistance);
        maxTargetRenderDistance = Math.max(config.minRenderDistance, maxTargetRenderDistance);
        
        int newRenderDistance = currentRenderDistance;
        
        // Hysteresis: require multiple consecutive signals before changing
        if (averageFPS < targetFPS * 0.8) {
            consecutiveLowFpsCount++;
            consecutiveHighFpsCount = 0;
            
            if (consecutiveLowFpsCount >= HYSTERESIS_THRESHOLD) {
                newRenderDistance = Math.max(config.minRenderDistance, currentRenderDistance - 1);
                if (newRenderDistance != currentRenderDistance) {
                    LOGGER.debug("FPS too low ({} < {}), reducing render distance from {} to {}", 
                        (int)averageFPS, (int)(targetFPS * 0.8), currentRenderDistance, newRenderDistance);
                    consecutiveLowFpsCount = 0; // Reset after change
                }
            }
        } else if (averageFPS > targetFPS * 1.2 && currentRenderDistance < maxTargetRenderDistance) {
            consecutiveHighFpsCount++;
            consecutiveLowFpsCount = 0;
            
            if (consecutiveHighFpsCount >= HYSTERESIS_THRESHOLD) {
                newRenderDistance = Math.min(maxTargetRenderDistance, currentRenderDistance + 1);
                if (newRenderDistance != currentRenderDistance) {
                    LOGGER.debug("FPS good ({} > {}), increasing render distance from {} to {}", 
                        (int)averageFPS, (int)(targetFPS * 1.2), currentRenderDistance, newRenderDistance);
                    consecutiveHighFpsCount = 0; // Reset after change
                }
            }
        } else {
            // FPS is in acceptable range, reset counters
            consecutiveLowFpsCount = 0;
            consecutiveHighFpsCount = 0;
        }
        
        if (newRenderDistance != currentRenderDistance) {
            currentRenderDistance = newRenderDistance;
            applyRenderDistance();
        }
    }
    
    private static void applyRenderDistance() {
        OptidumConfig config = ConfigManager.getConfig();
        if (config.enableDebugLogging) {
            LOGGER.debug("Setting render distance to {}", currentRenderDistance);
        }
        
        // Delegate to the client-side applier
        if (applier != null) {
            try {
                applier.apply(currentRenderDistance);
                lastAppliedRenderDistance = currentRenderDistance;
            } catch (Exception e) {
                LOGGER.warn("Failed to apply render distance", e);
            }
        }
        
        // If Sodium is loaded, we might need to coordinate with its render system
        if (Optidum.isSodiumLoaded && config.adjustForSodiumRendering) {
            applySodiumRenderDistance(currentRenderDistance);
        }
    }
    
    private static void applySodiumRenderDistance(int distance) {
        OptidumConfig config = ConfigManager.getConfig();
        if (config.enableDebugLogging) {
            LOGGER.debug("Adjusting Sodium render distance to {}", distance);
        }
    }
    
    public static int getCurrentRenderDistance() {
        return currentRenderDistance > 0 ? currentRenderDistance : 8;
    }
    
    public static void setRenderDistance(int distance) {
        OptidumConfig config = ConfigManager.getConfig();
        if (distance >= config.minRenderDistance && distance <= config.maxRenderDistance) {
            currentRenderDistance = distance;
            preferredRenderDistance = distance;
            applyRenderDistance();
        }
    }
    
    public static void reset() {
        currentRenderDistance = -1;
        preferredRenderDistance = -1;
        lastAppliedRenderDistance = -1;
        frameTimes.clear();
        smoothedFPS = 60.0;
        lastFrameTime = 0;
        lastAdjustmentTime = 0;
        consecutiveLowFpsCount = 0;
        consecutiveHighFpsCount = 0;
    }
    
    public static void logStats() {
        OptidumConfig config = ConfigManager.getConfig();
        if (config.logPerformanceMetrics) {
            double avgFps = getAverageFPS();
            LOGGER.info("Render distance manager: current={}, target FPS={}, average FPS={}", 
                currentRenderDistance, config.targetFPS, String.format("%.1f", avgFps));
        }
    }
}