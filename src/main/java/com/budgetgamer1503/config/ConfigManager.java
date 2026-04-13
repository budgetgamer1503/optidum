package com.budgetgamer1503.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/Config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("optidum.json");
    
    private static OptidumConfig config;
    
    public static OptidumConfig getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }
    
    public static void loadConfig() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String content = Files.readString(CONFIG_PATH);
                config = GSON.fromJson(content, OptidumConfig.class);
                LOGGER.info("Loaded configuration from {}", CONFIG_PATH);
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
            Files.createDirectories(CONFIG_PATH.getParent());
            String content = GSON.toJson(config);
            Files.writeString(CONFIG_PATH, content);
            LOGGER.info("Saved configuration to {}", CONFIG_PATH);
        } catch (IOException e) {
            LOGGER.error("Failed to save configuration", e);
        }
    }
    
    public static void reloadConfig() {
        loadConfig();
    }
}