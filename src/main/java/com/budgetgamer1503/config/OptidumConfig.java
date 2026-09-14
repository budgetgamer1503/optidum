package com.budgetgamer1503.config;

public class OptidumConfig {

    public boolean entityAiThrottle = true;
    public int entityAiDistance = 32;
    public int entityAiReductionFactor = 3;
    public boolean optimizeHostileEntities = true;
    public boolean optimizePassiveEntities = true;
    public boolean optimizeAmbientEntities = true;

    public boolean chunkLoadingOptimization = true;
    public boolean prioritizeViewDirection = true;
    public boolean preventBorderChurn = true;

    public boolean networkOptimization = true;
    public boolean packetBatching = true;

    public boolean memoryOptimization = true;
    public boolean enableMemoryPressureMonitoring = true;
    public boolean dimensionCacheCleanup = true;

    public boolean dynamicFpsEnabled = true;
    public int unfocusedFpsLimit = 15;

    public boolean sodiumIntegration = true;
    public boolean sodiumPerformanceMode = true;
    public boolean sodiumReduceVisualEffects = false;
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

    public boolean enableDebugLogging = false;

    public void validate() {
        if (entityAiDistance < 8) {
            entityAiDistance = 8;
        } else if (entityAiDistance > 128) {
            entityAiDistance = 128;
        }

        if (entityAiReductionFactor < 2) {
            entityAiReductionFactor = 2;
        } else if (entityAiReductionFactor > 10) {
            entityAiReductionFactor = 10;
        }

        if (unfocusedFpsLimit < 5) {
            unfocusedFpsLimit = 5;
        } else if (unfocusedFpsLimit > 60) {
            unfocusedFpsLimit = 60;
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
    }
}