package com.budgetgamer1503.optimization;

import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MemoryOptimizer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/MemoryOptimizer");
    private static final Runtime RUNTIME = Runtime.getRuntime();

    private static long lastCheckTime = 0;
    private static final long CHECK_INTERVAL_MS = 15000;
    private static volatile boolean highPressureAlerted = false;

    public enum MemoryTrimLevel {
        MODERATE,
        CRITICAL,
        DIMENSION_CHANGE
    }

    @FunctionalInterface
    public interface MemoryTrimCallback {
        void onTrimMemory(MemoryTrimLevel level);
    }

    private static final List<MemoryTrimCallback> TRIM_CALLBACKS = new CopyOnWriteArrayList<>();

    static {
        registerTrimCallback(level -> {
            boolean fullClear = (level == MemoryTrimLevel.DIMENSION_CHANGE || level == MemoryTrimLevel.CRITICAL);
            ChunkOptimizer.trimMemory(fullClear);
        });
    }

    public static void registerTrimCallback(MemoryTrimCallback callback) {
        if (callback != null) {
            TRIM_CALLBACKS.add(callback);
        }
    }

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

        double usage = (double) used / (max > 0 ? max : total);

        if (usage > 0.85) {
            if (!highPressureAlerted) {
                LOGGER.warn("[Optidum] High JVM memory pressure ({}% used). Performing active cache eviction and adaptive throttling.",
                    (int) (usage * 100));
                highPressureAlerted = true;
            }
            trimCaches(usage > 0.92 ? MemoryTrimLevel.CRITICAL : MemoryTrimLevel.MODERATE);

            if (config.forceSystemGc) {
                LOGGER.debug("[Optidum] Explicit System.gc() invoked via configuration");
                System.gc();
            }
        } else if (usage < 0.75) {
            highPressureAlerted = false;
        }

        if (config.enableDebugLogging) {
            LOGGER.debug("[Optidum] Memory check: {} MB / {} MB ({}%)",
                used / (1024 * 1024), max / (1024 * 1024), (int) (usage * 100));
        }
    }

    public static void onDimensionChange() {
        OptidumConfig config = ConfigManager.getConfig();
        if (!config.memoryOptimization || !config.dimensionCacheCleanup) {
            return;
        }

        LOGGER.info("[Optidum] Dimension changed - evicting stale level caches and tracking records");
        trimCaches(MemoryTrimLevel.DIMENSION_CHANGE);

        if (config.forceSystemGc) {
            LOGGER.debug("[Optidum] Explicit System.gc() invoked on dimension change via configuration");
            System.gc();
        }
    }

    public static void trimCaches(MemoryTrimLevel level) {
        for (MemoryTrimCallback callback : TRIM_CALLBACKS) {
            try {
                callback.onTrimMemory(level);
            } catch (Throwable t) {
                LOGGER.debug("[Optidum] Error in memory trim callback", t);
            }
        }
    }

    public static boolean isHighPressureAlerted() {
        return highPressureAlerted;
    }

    public static void setHighPressureAlertedForTesting(boolean alerted) {
        highPressureAlerted = alerted;
    }
}
