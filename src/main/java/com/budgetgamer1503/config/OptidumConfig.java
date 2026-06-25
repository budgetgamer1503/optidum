package com.budgetgamer1503.config;

@SuppressWarnings("unused")
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
    public boolean enableMemoryPressureMonitoring = true;
    
    // Render Distance Optimization
    public boolean renderDistanceOptimization = true;
    public int targetFPS = 60;
    public int minRenderDistance = 4;
    public int maxRenderDistance = 16;
    
    // Sodium Compatibility
    public boolean sodiumIntegration = true;
    public boolean adjustForSodiumRendering = true;
    public boolean useSodiumChunkManagement = true;
    public boolean sodiumPerformanceMode = true;
    public boolean sodiumReduceVisualEffects = true;
    public boolean sodiumDeferChunkUpdates = true;
    public boolean sodiumAnimateOnlyVisibleTextures = true;
    public boolean sodiumUseEntityCulling = true;
    public boolean sodiumUseFogOcclusion = true;
    public boolean sodiumUseBlockFaceCulling = true;
    public boolean sodiumUseCompactVertexFormat = true;
    public boolean sodiumUsePersistentMapping = true;
    public boolean sodiumUseChunkMultidraw = true;
    public int sodiumCpuRenderAheadLimit = 2;
    public int sodiumChunkBuilderThreads = 0;
    public boolean reduceRenderDistanceOnLagSpike = true;
    public int lagSpikeFrameTimeMs = 125;
    public int lagSpikeCooldownSeconds = 20;
    
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
        
        if (targetFPS < 30) {
            targetFPS = 30;
        } else if (targetFPS > 240) {
            targetFPS = 240;
        }
        
        if (sodiumCpuRenderAheadLimit < 1) {
            sodiumCpuRenderAheadLimit = 1;
        } else if (sodiumCpuRenderAheadLimit > 5) {
            sodiumCpuRenderAheadLimit = 5;
        }
        
        if (sodiumChunkBuilderThreads < 0) {
            sodiumChunkBuilderThreads = 0;
        } else if (sodiumChunkBuilderThreads > 16) {
            sodiumChunkBuilderThreads = 16;
        }
        
        if (lagSpikeFrameTimeMs < 50) {
            lagSpikeFrameTimeMs = 50;
        } else if (lagSpikeFrameTimeMs > 1000) {
            lagSpikeFrameTimeMs = 1000;
        }
        
        if (lagSpikeCooldownSeconds < 5) {
            lagSpikeCooldownSeconds = 5;
        } else if (lagSpikeCooldownSeconds > 300) {
            lagSpikeCooldownSeconds = 300;
        }
    }
}