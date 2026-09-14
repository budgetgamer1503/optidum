package com.budgetgamer1503.client.sodium;

import com.budgetgamer1503.Optidum;
import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.structure.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class OptidumSodiumConfig implements ConfigEntryPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/SodiumConfig");

    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        try {
            OptionPageBuilder page = builder.createOptionPage();
            page.setName(Component.literal("Optidum"));

            OptionGroupBuilder aiGroup = builder.createOptionGroup();
            aiGroup.setName(Component.literal("Entity AI Optimization"));
            addToggle(builder, aiGroup, "entity_ai_throttle", "Entity AI Throttle",
                "Reduces AI pathfinding calculations for far-away mobs without affecting physics or items.",
                cfg -> cfg.entityAiThrottle, (cfg, val) -> cfg.entityAiThrottle = val, true);
            addToggle(builder, aiGroup, "optimize_hostiles", "Optimize Hostile Mobs",
                "Allows distant hostile mobs to evaluate pathfinding less frequently.",
                cfg -> cfg.optimizeHostileEntities, (cfg, val) -> cfg.optimizeHostileEntities = val, true);
            addToggle(builder, aiGroup, "optimize_passives", "Optimize Passive Animals",
                "Allows distant farm animals to evaluate pathfinding less frequently.",
                cfg -> cfg.optimizePassiveEntities, (cfg, val) -> cfg.optimizePassiveEntities = val, true);
            addToggle(builder, aiGroup, "optimize_ambients", "Optimize Ambient Creatures",
                "Allows distant bats and ambient mobs to evaluate AI less frequently.",
                cfg -> cfg.optimizeAmbientEntities, (cfg, val) -> cfg.optimizeAmbientEntities = val, true);
            page.addOptionGroup(aiGroup);

            OptionGroupBuilder chunkGroup = builder.createOptionGroup();
            chunkGroup.setName(Component.literal("Chunk Loading Optimization"));
            addToggle(builder, chunkGroup, "chunk_optimization", "Chunk Optimization",
                "Enables Optidum smart chunk tracking and prioritization.",
                cfg -> cfg.chunkLoadingOptimization, (cfg, val) -> cfg.chunkLoadingOptimization = val, true);
            addToggle(builder, chunkGroup, "view_priority", "View Direction Priority",
                "Prioritizes loading chunks in the direction the player is looking.",
                cfg -> cfg.prioritizeViewDirection, (cfg, val) -> cfg.prioritizeViewDirection = val, true);
            addToggle(builder, chunkGroup, "border_guard", "Border Churn Guard",
                "Prevents rapid chunk load/unload loops when moving near borders.",
                cfg -> cfg.preventBorderChurn, (cfg, val) -> cfg.preventBorderChurn = val, true);
            page.addOptionGroup(chunkGroup);

            OptionGroupBuilder netGroup = builder.createOptionGroup();
            netGroup.setName(Component.literal("Network Optimization"));
            addToggle(builder, netGroup, "network_optimization", "Network Optimization",
                "Enables Netty packet flush consolidation to reduce socket syscalls.",
                cfg -> cfg.networkOptimization, (cfg, val) -> cfg.networkOptimization = val, true);
            addToggle(builder, netGroup, "packet_batching", "Packet Flush Batching",
                "Consolidates small rapid packets into batched Netty flushes.",
                cfg -> cfg.packetBatching, (cfg, val) -> cfg.packetBatching = val, true);
            page.addOptionGroup(netGroup);

            OptionGroupBuilder memGroup = builder.createOptionGroup();
            memGroup.setName(Component.literal("Memory & Dynamic FPS"));
            addToggle(builder, memGroup, "dynamic_fps", "Dynamic FPS (Inactive Throttle)",
                "Reduces framerate when the Minecraft window is unfocused or minimized to save power and heat.",
                cfg -> cfg.dynamicFpsEnabled, (cfg, val) -> cfg.dynamicFpsEnabled = val, true);
            addToggle(builder, memGroup, "memory_monitoring", "Memory Pressure Monitor",
                "Monitors JVM memory pressure and clears caches when high.",
                cfg -> cfg.enableMemoryPressureMonitoring, (cfg, val) -> cfg.enableMemoryPressureMonitoring = val, true);
            addToggle(builder, memGroup, "dimension_cleanup", "Dimension Cache Cleanup",
                "Releases stale chunk and texture caches when switching dimensions.",
                cfg -> cfg.dimensionCacheCleanup, (cfg, val) -> cfg.dimensionCacheCleanup = val, true);
            page.addOptionGroup(memGroup);

            OptionGroupBuilder sodGroup = builder.createOptionGroup();
            sodGroup.setName(Component.literal("Sodium Optimization Profile"));
            addToggle(builder, sodGroup, "defer_chunks", "Always Defer Chunk Updates",
                "Defers heavy chunk rebuilds to eliminate frame-time spikes.",
                cfg -> cfg.sodiumDeferChunkUpdates, (cfg, val) -> cfg.sodiumDeferChunkUpdates = val, true);
            addToggle(builder, sodGroup, "compact_vertex", "Use Compact Vertex Format",
                "Reduces GPU memory bandwidth using compact vertex buffers.",
                cfg -> cfg.sodiumUseCompactVertexFormat, (cfg, val) -> cfg.sodiumUseCompactVertexFormat = val, true);
            addToggle(builder, sodGroup, "block_face_culling", "Use Block Face Culling",
                "Allows Sodium to cull hidden block faces.",
                cfg -> cfg.sodiumUseBlockFaceCulling, (cfg, val) -> cfg.sodiumUseBlockFaceCulling = val, true);
            page.addOptionGroup(sodGroup);

            ModOptionsBuilder modOptions = builder.registerModOptions(Optidum.MOD_ID);
            modOptions.setName("Optidum");
            modOptions.setVersion("1.5");
            try {
                modOptions.setIcon(Identifier.fromNamespaceAndPath(Optidum.MOD_ID, "icon.png"));
            } catch (Throwable ignored) {}
            modOptions.addPage(page);

            LOGGER.info("Successfully registered Optidum configuration page into Sodium Video Settings");
        } catch (Throwable t) {
            LOGGER.error("Failed to register Optidum Sodium configuration", t);
        }
    }

    private void addToggle(
        ConfigBuilder builder,
        OptionGroupBuilder group,
        String id,
        String name,
        String tooltip,
        Function<OptidumConfig, Boolean> getter,
        BiConsumer<OptidumConfig, Boolean> setter,
        boolean defaultValue
    ) {
        try {
            BooleanOptionBuilder option = builder.createBooleanOption(
                Identifier.fromNamespaceAndPath(Optidum.MOD_ID, id)
            );
            option.setName(Component.literal(name));
            option.setTooltip(Component.literal(tooltip));
            option.setDefaultValue(defaultValue);
            option.setStorageHandler(ConfigManager::saveConfig);
            option.setBinding(
                val -> {
                    setter.accept(ConfigManager.getConfig(), val);
                    ConfigManager.saveConfig();
                },
                () -> getter.apply(ConfigManager.getConfig())
            );
            group.addOption(option);
        } catch (Throwable t) {
            LOGGER.warn("Failed to create toggle option {}: {}", name, t.getMessage());
        }
    }
}
