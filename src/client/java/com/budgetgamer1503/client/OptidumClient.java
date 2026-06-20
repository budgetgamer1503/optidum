package com.budgetgamer1503.client;

import com.budgetgamer1503.Optidum;
import com.budgetgamer1503.client.config.OptidumConfigScreen;
import com.budgetgamer1503.client.optimization.SodiumOptimizer;
import com.budgetgamer1503.optimization.RenderDistanceManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;

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
            if (!isVideoSettingsScreen(screen)) {
                return;
            }
            
            int buttonWidth = 108;
            int buttonHeight = 20;
            int x = 8;
            int y = Math.max(6, scaledHeight - buttonHeight - 8);
            Button button = Button.builder(
                Component.literal("Optidum..."),
                clicked -> openConfigScreen(client, screen)
            ).bounds(x, y, buttonWidth, buttonHeight).build();
            addClickableWidget(screen, button);
            ScreenMouseEvents.allowMouseClick(screen).register((clickedScreen, event) -> {
                if (button.isMouseOver(event.x(), event.y())) {
                    openConfigScreen(client, screen);
                    return false;
                }
                return true;
            });
            LOGGER.info("Added Optidum config button to {}", screen.getClass().getName());
        });
    }
    
    private void openConfigScreen(Minecraft client, Screen parent) {
        LOGGER.info("Opening Optidum config screen from {}", parent.getClass().getName());
        client.submit(() -> client.setScreen(new OptidumConfigScreen(parent)));
    }
    
    @SuppressWarnings("unchecked")
    private void addClickableWidget(Screen screen, Button button) {
        Screens.getWidgets(screen).add(button);
        List<GuiEventListener> children = (List<GuiEventListener>) screen.children();
        if (!children.contains(button)) {
            children.add(button);
        }
    }
    
    private boolean isVideoSettingsScreen(Screen screen) {
        if (screen instanceof VideoSettingsScreen) {
            return true;
        }
        
        String className = screen.getClass().getName().toLowerCase(Locale.ROOT);
        return className.contains("sodium") && className.contains("options");
    }
}