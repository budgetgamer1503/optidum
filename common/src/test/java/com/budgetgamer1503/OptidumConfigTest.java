package com.budgetgamer1503;

import com.budgetgamer1503.config.OptidumConfig;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OptidumConfigTest {

    @Test
    public void testDefaultConfigValues() {
        OptidumConfig config = new OptidumConfig();
        assertTrue(config.entityAiThrottle);
        assertEquals(32, config.entityAiDistance);
        assertEquals(3, config.entityAiReductionFactor);
        assertTrue(config.chunkLoadingOptimization);
        assertTrue(config.prioritizeViewDirection);
        assertTrue(config.preventBorderChurn);
        assertTrue(config.networkOptimization);
        assertTrue(config.packetBatching);
        assertTrue(config.memoryOptimization);
        assertTrue(config.dynamicFpsEnabled);
        assertEquals(15, config.unfocusedFpsLimit);
        assertTrue(config.sodiumIntegration);
    }

    @Test
    public void testConfigClamping() {
        OptidumConfig config = new OptidumConfig();
        config.entityAiDistance = 2;
        config.entityAiReductionFactor = 1;
        config.unfocusedFpsLimit = 2;
        config.sodiumCpuRenderAheadLimit = 0;
        config.sodiumChunkBuilderThreads = -5;

        config.validate();

        assertEquals(8, config.entityAiDistance);
        assertEquals(2, config.entityAiReductionFactor);
        assertEquals(5, config.unfocusedFpsLimit);
        assertEquals(1, config.sodiumCpuRenderAheadLimit);
        assertEquals(0, config.sodiumChunkBuilderThreads);

        config.entityAiDistance = 200;
        config.entityAiReductionFactor = 25;
        config.unfocusedFpsLimit = 120;
        config.sodiumCpuRenderAheadLimit = 10;
        config.sodiumChunkBuilderThreads = 32;

        config.validate();

        assertEquals(128, config.entityAiDistance);
        assertEquals(10, config.entityAiReductionFactor);
        assertEquals(60, config.unfocusedFpsLimit);
        assertEquals(5, config.sodiumCpuRenderAheadLimit);
        assertEquals(16, config.sodiumChunkBuilderThreads);
    }

    @Test
    public void testJsonSerialization() {
        Gson gson = new Gson();
        OptidumConfig config = new OptidumConfig();
        config.entityAiDistance = 64;
        config.unfocusedFpsLimit = 30;

        String json = gson.toJson(config);
        OptidumConfig deserialized = gson.fromJson(json, OptidumConfig.class);

        assertNotNull(deserialized);
        assertEquals(64, deserialized.entityAiDistance);
        assertEquals(30, deserialized.unfocusedFpsLimit);
    }
}
