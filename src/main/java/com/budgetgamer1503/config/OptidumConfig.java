package com.budgetgamer1503.config;

public class OptidumConfig {
    
    // Entity Tick Optimization
    public boolean entityTickOptimization = true;
    public int entityTickDistance = 32;
    public int entityTickReductionFactor = 3;
    public boolean optimizeHostileEntities = true;
    public boolean optimizePassiveEntities = true;
    public boolean optimizeAmbientEntities = true;
    
    // Chunk Loading Optimization
    public boolean chunkLoadingOptimization = true;
    public int smartChunkLoadDistance = 12;
    public boolean prioritizeViewDirection = true;
    public boolean unloadDistantChunks = true;
    
    // Network Optimization
    public boolean networkOptimization = true;
    public int packetCompressionThreshold = 500;
    public boolean aggregateSmallPackets = true;
    public boolean enablePacketCaching = true;
    
    // Memory Optimization
    public boolean memoryOptimization = true;
    public boolean entityPooling = true;
    public int entityPoolSize = 100;
    public boolean enableGarbageCollectionOptimization = true;
    
    // Render Distance Optimization
    public boolean renderDistanceOptimization = true;
    public int targetFPS = 60;
    public int minRenderDistance = 4;
    public int maxRenderDistance = 16;
    
    // Sodium Compatibility
    public boolean sodiumIntegration = true;
    public boolean adjustForSodiumRendering = true;
    public boolean useSodiumChunkManagement = true;
    
    // Debug & Logging
    public boolean enableDebugLogging = false;
    public boolean logPerformanceMetrics = false;
    public int metricsLogInterval = 60;
    
    public void validate() {
        // Ensure minRenderDistance is not greater than maxRenderDistance
        if (minRenderDistance > maxRenderDistance) {
            minRenderDistance = maxRenderDistance;
        }
        
        // Ensure entityTickDistance is reasonable
        if (entityTickDistance < 1) {
            entityTickDistance = 1;
        }
        
        // Ensure packet compression threshold is reasonable
        if (packetCompressionThreshold < 100) {
            packetCompressionThreshold = 100;
        }
        
        // Ensure reduction factor is reasonable
        if (entityTickReductionFactor < 1) {
            entityTickReductionFactor = 1;
        } else if (entityTickReductionFactor > 10) {
            entityTickReductionFactor = 10;
        }
        
        // Ensure pool size is reasonable
        if (entityPoolSize < 10) {
            entityPoolSize = 10;
        } else if (entityPoolSize > 1000) {
            entityPoolSize = 1000;
        }
    }
}