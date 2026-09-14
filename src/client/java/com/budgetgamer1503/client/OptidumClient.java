package com.budgetgamer1503.client;

import com.budgetgamer1503.Optidum;
import com.budgetgamer1503.client.config.OptidumConfigScreen;
import com.budgetgamer1503.client.optimization.SodiumOptimizer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OptidumClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/Client");

    @Override
    public void onInitializeClient() {
        LOGGER.info("Optidum client initializing...");

        Optidum.isSodiumLoaded = FabricLoader.getInstance().isModLoaded("sodium");
        if (Optidum.isSodiumLoaded) {
            SodiumOptimizer.applyPerformanceProfile();
            LOGGER.info("Sodium detected - Optidum Sodium Video Settings tab active via ConfigEntryPoint");
        } else {
            LOGGER.info("Sodium not detected - standalone config fallback available");
        }

        if (FabricLoader.getInstance().isModLoaded("dynamic_fps")) {
            LOGGER.info("Dynamic FPS mod detected - Optidum will coordinate with it");
        }

        registerVanillaVideoSettingsButton();

        LOGGER.info("Optidum client successfully initialized!");
    }

    private void registerVanillaVideoSettingsButton() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            // Only add fallback button on vanilla VideoSettingsScreen if Sodium GUI is not replacing it
            if (!(screen instanceof VideoSettingsScreen)) {
                return;
            }

            int buttonWidth = 98;
            int buttonHeight = 20;
            int x = 8;
            int y = Math.max(6, scaledHeight - buttonHeight - 8);

            Button button = Button.builder(
                Component.literal("Optidum..."),
                clicked -> openConfigScreen(client, screen)
            ).bounds(x, y, buttonWidth, buttonHeight).build();

            Screens.getWidgets(screen).add(button);
        });
    }

    private void openConfigScreen(Minecraft client, Screen parent) {
        LOGGER.info("Opening Optidum config screen");
        client.submit(() -> client.gui.setScreen(new OptidumConfigScreen(parent)));
    }
}