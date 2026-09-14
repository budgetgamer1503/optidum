package com.budgetgamer1503.optimization;

import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryOptimizer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/MemoryOptimizer");
    private static final Runtime RUNTIME = Runtime.getRuntime();
    
    private static long lastCheckTime = 0;
    private static final long CHECK_INTERVAL_MS = 30000;
    private static boolean highPressureAlerted = false;

    public static void periodicCheck() {
        OptidumConfig config = ConfigManager.getConfig();
        if (!config.memoryOptimization || !config.enableMemoryPressureMonitoring) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastCheckTime < CHECK_INTERVAL_MS) {
            return;
        }
        lastCheckTime = now;

        long max = RUNTIME.maxMemory();
        long total = RUNTIME.totalMemory();
        long free = RUNTIME.freeMemory();
        long used = total - free;

        double usage = (double) used / max;

        if (usage > 0.88) {
            if (!highPressureAlerted) {
                LOGGER.warn("High JVM memory pressure ({}% of {} MB used). Suggesting garbage collection sweep.",
                    (int) (usage * 100), max / (1024 * 1024));
                highPressureAlerted = true;
            }
            System.gc();
        } else {
            highPressureAlerted = false;
        }

        if (config.enableDebugLogging) {
            LOGGER.debug("Memory check: {} MB / {} MB ({}%)",
                used / (1024 * 1024), max / (1024 * 1024), (int) (usage * 100));
        }
    }

    public static void onDimensionChange() {
        OptidumConfig config = ConfigManager.getConfig();
        if (config.memoryOptimization && config.dimensionCacheCleanup) {
            LOGGER.info("Dimension changed - releasing unused level caches");
            System.gc();
        }
    }
}