package com.budgetgamer1503.client;

import com.budgetgamer1503.Optidum;
import com.budgetgamer1503.client.optimization.SodiumOptimizer;
import com.budgetgamer1503.optimization.RenderDistanceManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OptidumClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/Client");
    
    @Override
    public void onInitializeClient() {
        LOGGER.info("Optidum client initializing...");
        
        // Register the render distance applier callback
        RenderDistanceManager.setApplier(renderDistance -> {
            Minecraft client = Minecraft.getInstance();
            if (client != null && client.options != null) {
                client.options.renderDistance().set(renderDistance);
            }
        });
        
        // Sodium is a hard dependency; apply the performance profile once it is available.
        Optidum.isSodiumLoaded = FabricLoader.getInstance().isModLoaded("sodium");
        if (Optidum.isSodiumLoaded) {
            SodiumOptimizer.applyPerformanceProfile();
        } else {
            LOGGER.error("Sodium is required for Optidum's client performance profile");
        }
        
        LOGGER.info("Optidum client initialized!");
        LOGGER.info("Sodium compatibility: {}", Optidum.isSodiumLoaded ? "REQUIRED AND ENABLED" : "MISSING");
        LOGGER.info("Render distance optimization ready");
    }
}