package com.budgetgamer1503.client.config;

import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class OptidumConfigScreen extends OptionsSubScreen {

    public OptidumConfigScreen(Screen parent) {
        super(parent, Minecraft.getInstance().options, Component.literal("Optidum Configuration"));
    }

    @Override
    protected void addOptions() {
        OptidumConfig config = ConfigManager.getConfig();

        // Section: Entity AI
        this.list.addHeader(Component.literal("Entity AI Optimization"));
        this.list.addSmall(
            OptionInstance.createBoolean(
                "optidum.options.entity_ai_throttle",
                OptionInstance.cachedConstantTooltip(Component.literal("Throttles pathfinding calculations for distant mobs without affecting physics or items.")),
                config.entityAiThrottle,
                val -> { config.entityAiThrottle = val; ConfigManager.saveConfig(); }
            ),
            OptionInstance.createBoolean(
                "optidum.options.optimize_hostiles",
                OptionInstance.cachedConstantTooltip(Component.literal("Allows far-away hostile mobs to evaluate pathfinding less frequently.")),
                config.optimizeHostileEntities,
                val -> { config.optimizeHostileEntities = val; ConfigManager.saveConfig(); }
            )
        );
        this.list.addSmall(
            OptionInstance.createBoolean(
                "optidum.options.optimize_passives",
                OptionInstance.cachedConstantTooltip(Component.literal("Allows distant farm animals to evaluate pathfinding less frequently.")),
                config.optimizePassiveEntities,
                val -> { config.optimizePassiveEntities = val; ConfigManager.saveConfig(); }
            ),
            OptionInstance.createBoolean(
                "optidum.options.optimize_ambients",
                OptionInstance.cachedConstantTooltip(Component.literal("Allows distant bats and ambient mobs to evaluate AI less frequently.")),
                config.optimizeAmbientEntities,
                val -> { config.optimizeAmbientEntities = val; ConfigManager.saveConfig(); }
            )
        );

        // Section: Chunk Loading
        this.list.addHeader(Component.literal("Chunk Loading Optimization"));
        this.list.addSmall(
            OptionInstance.createBoolean(
                "optidum.options.chunk_loading",
                OptionInstance.cachedConstantTooltip(Component.literal("Enables Optidum smart chunk tracking and view direction prioritization.")),
                config.chunkLoadingOptimization,
                val -> { config.chunkLoadingOptimization = val; ConfigManager.saveConfig(); }
            ),
            OptionInstance.createBoolean(
                "optidum.options.view_priority",
                OptionInstance.cachedConstantTooltip(Component.literal("Prioritizes loading chunks in the direction the player is looking.")),
                config.prioritizeViewDirection,
                val -> { config.prioritizeViewDirection = val; ConfigManager.saveConfig(); }
            )
        );
        this.list.addSmall(
            OptionInstance.createBoolean(
                "optidum.options.border_guard",
                OptionInstance.cachedConstantTooltip(Component.literal("Prevents rapid chunk load/unload loops when moving back and forth across borders.")),
                config.preventBorderChurn,
                val -> { config.preventBorderChurn = val; ConfigManager.saveConfig(); }
            ),
            OptionInstance.createBoolean(
                "optidum.options.dynamic_fps",
                OptionInstance.cachedConstantTooltip(Component.literal("Reduces framerate when the Minecraft window is unfocused or minimized.")),
                config.dynamicFpsEnabled,
                val -> { config.dynamicFpsEnabled = val; ConfigManager.saveConfig(); }
            )
        );

        // Section: Network & Memory
        this.list.addHeader(Component.literal("Network & Memory Optimization"));
        this.list.addSmall(
            OptionInstance.createBoolean(
                "optidum.options.network_batching",
                OptionInstance.cachedConstantTooltip(Component.literal("Enables Netty packet flush consolidation to reduce network system call overhead.")),
                config.networkOptimization && config.packetBatching,
                val -> { config.networkOptimization = val; config.packetBatching = val; ConfigManager.saveConfig(); }
            ),
            OptionInstance.createBoolean(
                "optidum.options.memory_monitoring",
                OptionInstance.cachedConstantTooltip(Component.literal("Monitors JVM memory pressure and clears caches to prevent OutOfMemory errors.")),
                config.enableMemoryPressureMonitoring,
                val -> { config.enableMemoryPressureMonitoring = val; ConfigManager.saveConfig(); }
            )
        );

        // Section: Sodium Profile
        this.list.addHeader(Component.literal("Sodium Integration"));
        this.list.addSmall(
            OptionInstance.createBoolean(
                "optidum.options.defer_chunks",
                OptionInstance.cachedConstantTooltip(Component.literal("Always defers heavy chunk updates in Sodium to eliminate frame spikes.")),
                config.sodiumDeferChunkUpdates,
                val -> { config.sodiumDeferChunkUpdates = val; ConfigManager.saveConfig(); }
            ),
            OptionInstance.createBoolean(
                "optidum.options.compact_vertex",
                OptionInstance.cachedConstantTooltip(Component.literal("Uses compact vertex format in Sodium to reduce GPU memory bandwidth.")),
                config.sodiumUseCompactVertexFormat,
                val -> { config.sodiumUseCompactVertexFormat = val; ConfigManager.saveConfig(); }
            )
        );
    }

    @Override
    public void onClose() {
        ConfigManager.saveConfig();
        super.onClose();
    }
}
