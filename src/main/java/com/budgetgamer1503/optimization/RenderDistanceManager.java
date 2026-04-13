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
    private static final OptidumConfig config = ConfigManager.getConfig();
    
    // FPS tracking
    private static final Queue<Long> frameTimes = new LinkedList<>();
    private static final int FPS_SAMPLE_COUNT = 60;
    private static long lastFrameTime = 0;
    
    // Current render distance
    private static int currentRenderDistance = 8; // Default
    private static long lastAdjustmentTime = 0;
    private static final long ADJUSTMENT_COOLDOWN_MS = 5000; // 5 seconds
    
    // Smoothing
    private static final double SMOOTHING_FACTOR = 0.1;
    private static double smoothedFPS = 60.0;
    
    public static void onFrame() {
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
    
    private static double getAverageFPS() {
        if (frameTimes.isEmpty()) {
            return 60.0;
        }
        
        long sum = 0;
        for (Long fps : frameTimes) {
            sum += fps;
        }
        return (double) sum / frameTimes.size();
    }
    
    private static void adjustRenderDistance() {
        double averageFPS = getAverageFPS();
        double targetFPS = config.targetFPS;
        
        int newRenderDistance = currentRenderDistance;
        
        if (averageFPS < targetFPS * 0.8) {
            // FPS is too low, reduce render distance
            newRenderDistance = Math.max(config.minRenderDistance, currentRenderDistance - 1);
            if (newRenderDistance != currentRenderDistance) {
                LOGGER.debug("FPS too low ({} < {}), reducing render distance from {} to {}", 
                    (int)averageFPS, (int)(targetFPS * 0.8), currentRenderDistance, newRenderDistance);
            }
        } else if (averageFPS > targetFPS * 1.2 && currentRenderDistance < config.maxRenderDistance) {
            // FPS is good, increase render distance
            newRenderDistance = Math.min(config.maxRenderDistance, currentRenderDistance + 1);
            if (newRenderDistance != currentRenderDistance) {
                LOGGER.debug("FPS good ({} > {}), increasing render distance from {} to {}", 
                    (int)averageFPS, (int)(targetFPS * 1.2), currentRenderDistance, newRenderDistance);
            }
        }
        
        if (newRenderDistance != currentRenderDistance) {
            currentRenderDistance = newRenderDistance;
            applyRenderDistance();
        }
    }
    
    private static void applyRenderDistance() {
        // In a real implementation, this would adjust the game's render distance
        // For now, we just log it
        if (config.enableDebugLogging) {
            LOGGER.debug("Setting render distance to {}", currentRenderDistance);
        }
        
        // If Sodium is loaded, we might need to coordinate with its render system
        if (Optidum.isSodiumLoaded && config.adjustForSodiumRendering) {
            applySodiumRenderDistance(currentRenderDistance);
        }
    }
    
    private static void applySodiumRenderDistance(int distance) {
        // This would integrate with Sodium's API to adjust render distance
        // For now, just log that we would adjust Sodium's settings
        if (config.enableDebugLogging) {
            LOGGER.debug("Adjusting Sodium render distance to {}", distance);
        }
    }
    
    public static int getCurrentRenderDistance() {
        return currentRenderDistance;
    }
    
    public static void setRenderDistance(int distance) {
        if (distance >= config.minRenderDistance && distance <= config.maxRenderDistance) {
            currentRenderDistance = distance;
            applyRenderDistance();
        }
    }
    
    public static void reset() {
        currentRenderDistance = 8;
        frameTimes.clear();
        smoothedFPS = 60.0;
        lastFrameTime = 0;
        lastAdjustmentTime = 0;
    }
    
    public static void logStats() {
        if (config.logPerformanceMetrics) {
            LOGGER.info("Render distance manager: current={}, target FPS={}, average FPS={:.1f}", 
                currentRenderDistance, config.targetFPS, getAverageFPS());
        }
    }
}