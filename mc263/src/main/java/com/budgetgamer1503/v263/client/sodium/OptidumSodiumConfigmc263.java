package com.budgetgamer1503.v263.client.sodium;

import com.budgetgamer1503.Optidum;
import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class OptidumSodiumConfigmc263 implements ConfigEntryPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/SodiumConfig");

    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        try {
            Object page = builder.createOptionPage();
            setPageName(page, "Optidum");

            Object aiGroup = builder.createOptionGroup();
            setGroupName(aiGroup, "Entity AI Optimization");
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
            invokeMethod(page, "addOptionGroup", aiGroup);

            Object chunkGroup = builder.createOptionGroup();
            setGroupName(chunkGroup, "Chunk Loading Optimization");
            addToggle(builder, chunkGroup, "chunk_optimization", "Chunk Optimization",
                "Enables Optidum smart chunk tracking and prioritization.",
                cfg -> cfg.chunkLoadingOptimization, (cfg, val) -> cfg.chunkLoadingOptimization = val, true);
            addToggle(builder, chunkGroup, "view_priority", "View Direction Priority",
                "Prioritizes loading chunks in the direction the player is looking.",
                cfg -> cfg.prioritizeViewDirection, (cfg, val) -> cfg.prioritizeViewDirection = val, true);
            addToggle(builder, chunkGroup, "border_guard", "Border Churn Guard",
                "Prevents rapid chunk load/unload loops when moving near borders.",
                cfg -> cfg.preventBorderChurn, (cfg, val) -> cfg.preventBorderChurn = val, true);
            invokeMethod(page, "addOptionGroup", chunkGroup);

            Object netGroup = builder.createOptionGroup();
            setGroupName(netGroup, "Network Optimization");
            addToggle(builder, netGroup, "network_optimization", "Network Optimization",
                "Enables Netty packet flush consolidation to reduce socket syscalls.",
                cfg -> cfg.networkOptimization, (cfg, val) -> cfg.networkOptimization = val, true);
            addToggle(builder, netGroup, "packet_batching", "Packet Flush Batching",
                "Consolidates small rapid packets into batched Netty flushes.",
                cfg -> cfg.packetBatching, (cfg, val) -> cfg.packetBatching = val, true);
            invokeMethod(page, "addOptionGroup", netGroup);

            Object memGroup = builder.createOptionGroup();
            setGroupName(memGroup, "Memory & Dynamic FPS");
            addToggle(builder, memGroup, "dynamic_fps", "Dynamic FPS (Inactive Throttle)",
                "Reduces framerate when the Minecraft window is unfocused or minimized to save power and heat.",
                cfg -> cfg.dynamicFpsEnabled, (cfg, val) -> cfg.dynamicFpsEnabled = val, true);
            addToggle(builder, memGroup, "memory_monitoring", "Memory Pressure Monitor",
                "Monitors JVM memory pressure and clears caches when high.",
                cfg -> cfg.enableMemoryPressureMonitoring, (cfg, val) -> cfg.enableMemoryPressureMonitoring = val, true);
            addToggle(builder, memGroup, "dimension_cleanup", "Dimension Cache Cleanup",
                "Releases stale chunk and texture caches when switching dimensions.",
                cfg -> cfg.dimensionCacheCleanup, (cfg, val) -> cfg.dimensionCacheCleanup = val, true);
            invokeMethod(page, "addOptionGroup", memGroup);

            Object sodGroup = builder.createOptionGroup();
            setGroupName(sodGroup, "Sodium Optimization Profile");
            addToggle(builder, sodGroup, "defer_chunks", "Always Defer Chunk Updates",
                "Defers heavy chunk rebuilds to eliminate frame-time spikes.",
                cfg -> cfg.sodiumDeferChunkUpdates, (cfg, val) -> cfg.sodiumDeferChunkUpdates = val, true);
            addToggle(builder, sodGroup, "compact_vertex", "Use Compact Vertex Format",
                "Reduces GPU memory bandwidth using compact vertex buffers.",
                cfg -> cfg.sodiumUseCompactVertexFormat, (cfg, val) -> cfg.sodiumUseCompactVertexFormat = val, true);
            addToggle(builder, sodGroup, "block_face_culling", "Use Block Face Culling",
                "Allows Sodium to cull hidden block faces.",
                cfg -> cfg.sodiumUseBlockFaceCulling, (cfg, val) -> cfg.sodiumUseBlockFaceCulling = val, true);
            invokeMethod(page, "addOptionGroup", sodGroup);

            Object modOptions = builder.registerModOptions(Optidum.MOD_ID);
            invokeMethod(modOptions, "setName", "Optidum");
            invokeMethod(modOptions, "setVersion", Optidum.MOD_VERSION);
            try {
                Object iconId = createIdentifier(Optidum.MOD_ID, "icon.png");
                if (iconId != null) {
                    invokeMethod(modOptions, "setIcon", iconId);
                }
            } catch (Throwable ignored) {}
            invokeMethod(modOptions, "addPage", page);

            LOGGER.info("[Optidum] Registered Sodium Video Settings tab for mc263");
        } catch (Throwable t) {
            LOGGER.error("[Optidum] Failed to register Sodium configuration", t);
        }
    }

    private static Object createComponent(String text) {
        try {
            Class<?> compClass = Class.forName("net.minecraft.network.chat.Component");
            return compClass.getMethod("literal", String.class).invoke(null, text);
        } catch (Throwable t) {
            return text;
        }
    }

    private static Object createIdentifier(String namespace, String path) {
        try {
            Class<?> idClass = Class.forName("net.minecraft.resources.Identifier");
            return idClass.getMethod("fromNamespaceAndPath", String.class, String.class).invoke(null, namespace, path);
        } catch (Throwable t) {
            try {
                Class<?> idClass = Class.forName("net.minecraft.resources.ResourceLocation");
                return idClass.getMethod("fromNamespaceAndPath", String.class, String.class).invoke(null, namespace, path);
            } catch (Throwable ignored) {
                return null;
            }
        }
    }

    private static void setPageName(Object page, String name) {
        invokeFirstMatching(page, "setName", createComponent(name));
    }

    private static void setGroupName(Object group, String name) {
        invokeFirstMatching(group, "setName", createComponent(name));
    }

    private static void invokeFirstMatching(Object target, String methodName, Object arg) {
        for (Method m : target.getClass().getMethods()) {
            if (m.getName().equals(methodName) && m.getParameterCount() == 1) {
                try {
                    m.setAccessible(true);
                    m.invoke(target, arg);
                    return;
                } catch (Throwable ignored) {}
            }
        }
    }

    private static void invokeMethod(Object target, String methodName, Object... args) {
        for (Method m : target.getClass().getMethods()) {
            if (m.getName().equals(methodName) && m.getParameterCount() == args.length) {
                try {
                    m.setAccessible(true);
                    m.invoke(target, args);
                    return;
                } catch (Throwable ignored) {}
            }
        }
    }

    private void addToggle(
        ConfigBuilder builder,
        Object group,
        String id,
        String name,
        String tooltip,
        Function<OptidumConfig, Boolean> getter,
        BiConsumer<OptidumConfig, Boolean> setter,
        boolean defaultValue
    ) {
        try {
            Object idObj = createIdentifier(Optidum.MOD_ID, id);
            Object option = null;
            for (Method m : builder.getClass().getMethods()) {
                if (m.getName().equals("createBooleanOption") && m.getParameterCount() == 1) {
                    option = m.invoke(builder, idObj);
                    break;
                }
            }

            if (option == null) {
                return;
            }

            invokeFirstMatching(option, "setName", createComponent(name));
            invokeFirstMatching(option, "setTooltip", createComponent(tooltip));
            invokeMethod(option, "setDefaultValue", defaultValue);

            Runnable saveHandler = ConfigManager::saveConfig;
            for (Method m : option.getClass().getMethods()) {
                if (m.getName().equals("setStorageHandler")) {
                    m.invoke(option, saveHandler);
                    break;
                }
            }

            for (Method m : option.getClass().getMethods()) {
                if (m.getName().equals("setBinding")) {
                    java.util.function.Consumer<Boolean> consumer = val -> {
                        setter.accept(ConfigManager.getConfig(), val);
                        ConfigManager.saveConfig();
                    };
                    java.util.function.Supplier<Boolean> supplier = () -> getter.apply(ConfigManager.getConfig());
                    m.invoke(option, consumer, supplier);
                    break;
                }
            }

            invokeMethod(group, "addOption", option);
        } catch (Throwable t) {
            LOGGER.warn("[Optidum] Failed to create toggle option {}: {}", name, t.getMessage());
        }
    }
}
