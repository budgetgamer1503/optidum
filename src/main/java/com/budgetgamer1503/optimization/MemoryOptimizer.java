package com.budgetgamer1503.optimization;

import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MemoryOptimizer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/MemoryOptimizer");
    private static final OptidumConfig config = ConfigManager.getConfig();
    
    // Object pools
    private static final Map<Class<?>, ObjectPool<?>> objectPools = new HashMap<>();
    
    // Garbage collection tracking
    private static long lastGCTime = 0;
    private static long gcCount = 0;
    private static final long GC_INTERVAL_MS = 30000; // 30 seconds minimum between GC suggestions
    
    // Memory usage tracking
    private static final Runtime runtime = Runtime.getRuntime();
    private static long lastMemoryCheck = 0;
    private static final long MEMORY_CHECK_INTERVAL = 5000; // 5 seconds
    
    static {
        // Initialize object pools if enabled
        if (config.memoryOptimization && config.entityPooling) {
            initializeObjectPools();
        }
    }
    
    private static void initializeObjectPools() {
        // In a real implementation, we would pool common objects
        // For now, we just set up the structure
        LOGGER.info("Initializing object pools");
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T acquireObject(Class<T> clazz) {
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
        if (!config.memoryOptimization || !config.entityPooling || obj == null) {
            return;
        }
        
        @SuppressWarnings("unchecked")
        ObjectPool<T> pool = (ObjectPool<T>) objectPools.get(obj.getClass());
        if (pool != null) {
            pool.release(obj);
        }
    }
    
    public static void optimizeGarbageCollection() {
        if (!config.memoryOptimization || !config.enableGarbageCollectionOptimization) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        
        // Don't suggest GC too frequently
        if (currentTime - lastGCTime < GC_INTERVAL_MS) {
            return;
        }
        
        // Check memory usage
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        double usagePercentage = (double) usedMemory / maxMemory;
        
        // If memory usage is high, suggest GC
        if (usagePercentage > 0.85) { // 85% usage
            LOGGER.debug("High memory usage detected ({}%), suggesting garbage collection", 
                (int)(usagePercentage * 100));
            
            // In a real implementation, we might trigger a GC or optimize memory
            // For now, we just log it
            gcCount++;
            lastGCTime = currentTime;
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
        if (config.logPerformanceMetrics) {
            LOGGER.info("Memory optimizer stats: {} object pools, {} GC suggestions",
                objectPools.size(), gcCount);
            
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