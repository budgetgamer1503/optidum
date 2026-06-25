package com.budgetgamer1503.optimization;

import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings({"unused", "FieldCanBeLocal", "FieldMayBeFinal"})
public class MemoryOptimizer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/MemoryOptimizer");
    
    // Object pools
    private static final Map<Class<?>, ObjectPool<?>> objectPools = new HashMap<>();
    
    // Memory pressure tracking
    private static long lastMemoryPressureLogTime = 0;
    private static long memoryPressureEvents = 0;
    private static final long MEMORY_PRESSURE_LOG_INTERVAL_MS = 30000; // 30 seconds minimum between alerts
    
    // Memory usage tracking
    private static final Runtime runtime = Runtime.getRuntime();
    private static long lastMemoryCheck = 0;
    private static final long MEMORY_CHECK_INTERVAL = 5000; // 5 seconds
    
    private static boolean poolsInitialized = false;
    
    private static void ensurePoolsInitialized() {
        if (!poolsInitialized) {
            OptidumConfig config = ConfigManager.getConfig();
            if (config.memoryOptimization && config.entityPooling) {
                initializeObjectPools();
            }
            poolsInitialized = true;
        }
    }
    
    private static void initializeObjectPools() {
        LOGGER.info("Initializing object pools");
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T acquireObject(Class<T> clazz) {
        OptidumConfig config = ConfigManager.getConfig();
        if (!config.memoryOptimization || !config.entityPooling) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                LOGGER.error("Failed to create instance of {}", clazz.getName(), e);
                return null;
            }
        }
        
        ObjectPool<T> pool = (ObjectPool<T>) objectPools.get(clazz);
        if (pool == null) {
            pool = new ObjectPool<>(clazz, config.entityPoolSize);
            objectPools.put(clazz, pool);
        }
        
        T obj = pool.acquire();
        if (obj == null) {
            try {
                obj = clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                LOGGER.error("Failed to create instance of {}", clazz.getName(), e);
                return null;
            }
        }
        
        return obj;
    }
    
    public static <T> void releaseObject(T obj) {
        OptidumConfig config = ConfigManager.getConfig();
        if (!config.memoryOptimization || !config.entityPooling || obj == null) {
            return;
        }
        
        @SuppressWarnings("unchecked")
        ObjectPool<T> pool = (ObjectPool<T>) objectPools.get(obj.getClass());
        if (pool != null) {
            pool.release(obj);
        }
    }
    
    public static void monitorMemoryPressure() {
        OptidumConfig config = ConfigManager.getConfig();
        if (!config.memoryOptimization || !config.enableMemoryPressureMonitoring) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        
        // Don't log pressure warnings too frequently
        if (currentTime - lastMemoryPressureLogTime < MEMORY_PRESSURE_LOG_INTERVAL_MS) {
            return;
        }
        
        // Check memory usage
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        double usagePercentage = (double) usedMemory / maxMemory;
        
        if (usagePercentage > 0.85) { // 85% usage
            LOGGER.warn("High memory usage detected ({}%), monitoring memory pressure and object allocation",
                (int) (usagePercentage * 100));
            memoryPressureEvents++;
            lastMemoryPressureLogTime = currentTime;
        }
    }
    
    public static void checkMemoryUsage() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMemoryCheck < MEMORY_CHECK_INTERVAL) {
            return;
        }
        
        lastMemoryCheck = currentTime;
        
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        OptidumConfig config = ConfigManager.getConfig();
        if (config.logPerformanceMetrics) {
            LOGGER.info("Memory usage: {}/{} MB ({}% used), {} MB free",
                usedMemory / (1024 * 1024),
                maxMemory / (1024 * 1024),
                (int)((double) usedMemory / maxMemory * 100),
                freeMemory / (1024 * 1024));
        }
    }
    
    public static void clearObjectPools() {
        objectPools.clear();
        LOGGER.debug("Cleared all object pools");
    }
    
    public static void logStats() {
        OptidumConfig config = ConfigManager.getConfig();
        if (config.logPerformanceMetrics) {
            LOGGER.info("Memory optimizer stats: {} object pools, {} memory pressure alerts",
                objectPools.size(), memoryPressureEvents);
            
            for (Map.Entry<Class<?>, ObjectPool<?>> entry : objectPools.entrySet()) {
                ObjectPool<?> pool = entry.getValue();
                LOGGER.info("  Pool {}: {} available, {} created",
                    entry.getKey().getSimpleName(),
                    pool.availableCount(),
                    pool.totalCreated());
            }
        }
    }
    
    // Object pool implementation
    private static class ObjectPool<T> {
        private final Class<T> clazz;
        private final Queue<WeakReference<T>> pool = new ConcurrentLinkedQueue<>();
        private final int maxSize;
        private int createdCount = 0;
        
        public ObjectPool(Class<T> clazz, int maxSize) {
            this.clazz = clazz;
            this.maxSize = maxSize;
        }
        
        public T acquire() {
            WeakReference<T> ref;
            while ((ref = pool.poll()) != null) {
                T obj = ref.get();
                if (obj != null) {
                    return obj;
                }
            }
            return null;
        }
        
        public void release(T obj) {
            if (pool.size() < maxSize) {
                pool.offer(new WeakReference<>(obj));
            }
        }
        
        public int availableCount() {
            return (int) pool.stream()
                .map(WeakReference::get)
                .filter(Objects::nonNull)
                .count();
        }
        
        public int totalCreated() {
            return createdCount;
        }
    }
}