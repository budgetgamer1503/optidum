package com.budgetgamer1503.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/Config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    private static Path configPath = null;
    private static OptidumConfig config;
    
    public static Path getConfigPath() {
        if (configPath == null) {
            try {
                Class<?> flClass = Class.forName("net.fabricmc.loader.api.FabricLoader");
                Object instance = flClass.getMethod("getInstance").invoke(null);
                Path dir = (Path) flClass.getMethod("getConfigDir").invoke(instance);
                configPath = dir.resolve("optidum.json");
            } catch (Throwable t) {
                configPath = Path.of("config", "optidum.json");
            }
        }
        return configPath;
    }

    public static void setConfigPath(Path customPath) {
        configPath = customPath;
    }
    
    public static OptidumConfig getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }
    
    public static void loadConfig() {
        Path path = getConfigPath();
        if (Files.exists(path)) {
            try {
                String content = Files.readString(path);
                config = GSON.fromJson(content, OptidumConfig.class);
                if (config != null) {
                    config.validate();
                } else {
                    config = new OptidumConfig();
                }
                LOGGER.info("Loaded configuration from {}", path);
            } catch (IOException e) {
                LOGGER.error("Failed to load configuration", e);
                config = new OptidumConfig();
                saveConfig();
            }
        } else {
            config = new OptidumConfig();
            saveConfig();
        }
    }
    
    public static void saveConfig() {
        try {
            Path path = getConfigPath();
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            String content = GSON.toJson(config != null ? config : new OptidumConfig());
            Files.writeString(path, content);
            LOGGER.info("Saved configuration to {}", path);
        } catch (IOException e) {
            LOGGER.error("Failed to save configuration", e);
        }
    }
    
    public static void reloadConfig() {
        loadConfig();
    }
}
