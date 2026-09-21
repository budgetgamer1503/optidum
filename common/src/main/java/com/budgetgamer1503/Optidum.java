package com.budgetgamer1503;

import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.core.CompatibilityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Optidum {
    public static final String MOD_ID = "optidum";
    public static final String MOD_NAME = "Optidum";
    public static final String MOD_VERSION = "1.6";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean isSodiumLoaded = false;

    public static void init() {
        ConfigManager.loadConfig();
        CompatibilityManager.init();
        isSodiumLoaded = CompatibilityManager.isSodiumLoaded();

        LOGGER.info("[Optidum] Initialized v{}!", MOD_VERSION);
        LOGGER.info("[Optidum] Sodium compatibility: {}", isSodiumLoaded ? "ENABLED" : "DISABLED (Sodium not found)");
        LOGGER.info("[Optidum] Configuration loaded successfully");

        initializeOptimizations();
    }

    private static void initializeOptimizations() {
        var config = ConfigManager.getConfig();

        if (config.entityAiThrottle) {
            LOGGER.info("[Optidum] Entity AI optimization ENABLED (distance: {} blocks, reduction: {}x)",
                config.entityAiDistance, config.entityAiReductionFactor);
        }
        if (config.chunkLoadingOptimization) {
            LOGGER.info("[Optidum] Chunk loading optimization ENABLED (view direction priority: {}, border churn guard: {})",
                config.prioritizeViewDirection, config.preventBorderChurn);
        }
        if (config.networkOptimization) {
            LOGGER.info("[Optidum] Network packet batching ENABLED");
        }
        if (config.memoryOptimization) {
            LOGGER.info("[Optidum] Memory optimization ENABLED (pressure monitoring: {}, dimension cleanup: {})",
                config.enableMemoryPressureMonitoring, config.dimensionCacheCleanup);
        }
        if (config.dynamicFpsEnabled) {
            LOGGER.info("[Optidum] Dynamic FPS power saver ENABLED (unfocused limit: {} FPS)", config.unfocusedFpsLimit);
        }
        if (config.sodiumIntegration && isSodiumLoaded) {
            LOGGER.info("[Optidum] Sodium integration ENABLED");
        }
    }
}
