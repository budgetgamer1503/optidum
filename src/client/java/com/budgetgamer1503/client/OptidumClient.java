package com.budgetgamer1503.client;

import com.budgetgamer1503.Optidum;
import com.budgetgamer1503.client.config.OptidumConfigScreen;
import com.budgetgamer1503.client.optimization.SodiumOptimizer;
import com.budgetgamer1503.optimization.RenderDistanceManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OptidumClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/Client");
    
    @Override
    public void onInitializeClient() {
        LOGGER.info("Optidum client initializing...");
        
        // Register the render distance applier callback
        RenderDistanceManager.setApplier(renderDistance ->
            Minecraft.getInstance().options.renderDistance().set(renderDistance));
        
        // Sodium is a hard dependency; apply the performance profile once it is available.
        Optidum.isSodiumLoaded = FabricLoader.getInstance().isModLoaded("sodium");
        if (Optidum.isSodiumLoaded) {
            SodiumOptimizer.applyPerformanceProfile();
        } else {
            LOGGER.error("Sodium is required for Optidum's client performance profile");
        }
        
        registerVideoSettingsButton();
        
        LOGGER.info("Optidum client initialized!");
        LOGGER.info("Sodium compatibility: {}", Optidum.isSodiumLoaded ? "REQUIRED AND ENABLED" : "MISSING");
        LOGGER.info("Render distance optimization ready");
    }
    
    private void registerVideoSettingsButton() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof VideoSettingsScreen)) {
                return;
            }
            
            int buttonWidth = 108;
            int buttonHeight = 20;
            int x = Math.max(4, scaledWidth - buttonWidth - 8);
            int y = 6;
            Screens.getWidgets(screen).add(Button.builder(
                Component.literal("Optidum..."),
                button -> client.setScreen(new OptidumConfigScreen(screen))
            ).bounds(x, y, buttonWidth, buttonHeight).build());
        });
    }
}