package com.budgetgamer1503.v1171.client;

import com.budgetgamer1503.client.optimization.SodiumOptimizer;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OptidumClientmc1171 implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/Client");

    @Override
    public void onInitializeClient() {
        LOGGER.info("[Optidum] Client initializing for Minecraft 1.17.1...");
        SodiumOptimizer.applyPerformanceProfile();
        LOGGER.info("[Optidum] Client initialization complete for mc1171.");
    }
}
