package com.budgetgamer1503.client.optimization;

import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class SodiumOptimizer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/SodiumOptimizer");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private SodiumOptimizer() {
    }

    public static void applyPerformanceProfile() {
        OptidumConfig config = ConfigManager.getConfig();
        if (!config.sodiumIntegration || !config.sodiumPerformanceMode) {
            return;
        }

        boolean changed = tuneSodiumConfigFile(config);
        changed |= tuneMinecraftVideoOptions(config);

        if (changed) {
            LOGGER.info("[Optidum] Applied Sodium FPS and lag-spike performance profile");
        }
    }

    public static boolean tuneSodiumConfigFile(OptidumConfig config) {
        Path configDir = ConfigManager.getConfigPath().getParent();
        if (configDir == null) {
            configDir = Path.of("config");
        }
        Path path = configDir.resolve("sodium-options.json");
        JsonObject root = readJsonObject(path);
        JsonObject performance = group(root, "performance");
        JsonObject advanced = group(root, "advanced");
        JsonObject quality = group(root, "quality");

        boolean changed = false;
        changed |= put(performance, "always_defer_chunk_updates", config.sodiumDeferChunkUpdates);
        changed |= put(performance, "chunk_builder_threads", config.sodiumChunkBuilderThreads);
        changed |= put(performance, "animate_only_visible_textures", config.sodiumAnimateOnlyVisibleTextures);
        changed |= put(performance, "use_entity_culling", config.sodiumUseEntityCulling);
        changed |= put(performance, "use_fog_occlusion", config.sodiumUseFogOcclusion);
        changed |= put(performance, "use_block_face_culling", config.sodiumUseBlockFaceCulling);
        changed |= put(advanced, "cpu_render_ahead_limit", config.sodiumCpuRenderAheadLimit);
        changed |= put(advanced, "use_compact_vertex_format", config.sodiumUseCompactVertexFormat);
        changed |= put(advanced, "use_persistent_mapping", config.sodiumUsePersistentMapping);
        changed |= put(advanced, "use_chunk_multidraw", config.sodiumUseChunkMultidraw);

        if (config.sodiumReduceVisualEffects) {
            changed |= put(quality, "clouds", "OFF");
            changed |= put(quality, "weather_quality", "FAST");
            changed |= put(quality, "leaves_quality", "FAST");
            changed |= put(quality, "smooth_lighting", "LOW");
            changed |= put(quality, "enable_vignette", false);
            changed |= put(quality, "biome_blend_radius", 1);
        }

        if (!changed) {
            return false;
        }

        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.writeString(path, GSON.toJson(root));
            LOGGER.info("[Optidum] Updated Sodium configuration at {}", path);
            return true;
        } catch (IOException e) {
            LOGGER.warn("[Optidum] Failed to update Sodium configuration", e);
            return false;
        }
    }

    private static JsonObject readJsonObject(Path path) {
        if (!Files.exists(path)) {
            return new JsonObject();
        }

        try {
            JsonElement element = JsonParser.parseString(Files.readString(path));
            if (element != null && element.isJsonObject()) {
                return element.getAsJsonObject();
            }
        } catch (Exception e) {
            LOGGER.warn("[Optidum] Failed to read Sodium configuration; recreating profile", e);
        }

        return new JsonObject();
    }

    private static JsonObject group(JsonObject root, String key) {
        JsonElement element = root.get(key);
        if (element != null && element.isJsonObject()) {
            return element.getAsJsonObject();
        }

        JsonObject object = new JsonObject();
        root.add(key, object);
        return object;
    }

    private static boolean put(JsonObject object, String key, boolean value) {
        JsonElement current = object.get(key);
        if (current != null && current.isJsonPrimitive() && current.getAsBoolean() == value) {
            return false;
        }

        object.addProperty(key, value);
        return true;
    }

    private static boolean put(JsonObject object, String key, int value) {
        JsonElement current = object.get(key);
        if (current != null && current.isJsonPrimitive() && current.getAsInt() == value) {
            return false;
        }

        object.addProperty(key, value);
        return true;
    }

    private static boolean put(JsonObject object, String key, String value) {
        JsonElement current = object.get(key);
        if (current != null && current.isJsonPrimitive() && value.equals(current.getAsString())) {
            return false;
        }

        object.addProperty(key, value);
        return true;
    }

    public static boolean tuneMinecraftVideoOptions(OptidumConfig config) {
        try {
            Class<?> mcClass = null;
            try {
                mcClass = Class.forName("net.minecraft.client.Minecraft");
            } catch (ClassNotFoundException e) {
                try {
                    mcClass = Class.forName("net.minecraft.client.MinecraftClient");
                } catch (ClassNotFoundException ignored) {}
            }

            if (mcClass == null) {
                return false;
            }

            Method getInstance = mcClass.getMethod("getInstance");
            Object client = getInstance.invoke(null);
            if (client == null) {
                return false;
            }

            Object options = null;
            try {
                options = mcClass.getField("options").get(client);
            } catch (NoSuchFieldException e) {
                try {
                    options = mcClass.getMethod("getOptions").invoke(client);
                } catch (Throwable ignored) {}
            }

            if (options == null) {
                return false;
            }

            boolean changed = false;
            changed |= setOption(options, "biomeBlendRadius", 1);
            changed |= setOption(options, "entityDistanceScaling", 0.75D);

            if (config.sodiumReduceVisualEffects) {
                changed |= setOption(options, "cloudStatus", enumValue("net.minecraft.client.CloudStatus", "OFF"));
                changed |= setOption(options, "particles", enumValue("net.minecraft.client.ParticleStatus", "MINIMAL"));
                changed |= setOption(options, "graphicsMode", enumValue("net.minecraft.client.GraphicsStatus", "FAST"));
            }

            if (changed) {
                try {
                    options.getClass().getMethod("save").invoke(options);
                } catch (Throwable ignored) {}
            }

            return changed;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static Object enumValue(String className, String name) {
        try {
            Class<?> clazz = Class.forName(className);
            if (clazz.isEnum()) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                Object value = Enum.valueOf((Class<? extends Enum>) clazz, name);
                return value;
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static boolean setOption(Object options, String methodName, Object value) {
        if (value == null) {
            return false;
        }

        try {
            Method getter = options.getClass().getMethod(methodName);
            Object option = getter.invoke(options);
            Object current = option.getClass().getMethod("get").invoke(option);
            if (Objects.equals(current, value)) {
                return false;
            }

            option.getClass().getMethod("set", Object.class).invoke(option, value);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
