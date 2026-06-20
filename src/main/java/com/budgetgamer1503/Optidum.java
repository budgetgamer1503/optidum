package com.budgetgamer1503;

import com.budgetgamer1503.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Optidum implements ModInitializer {
	public static final String MOD_ID = "optidum";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	// Sodium compatibility flag
	public static boolean isSodiumLoaded = false;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		
		// Load configuration
		ConfigManager.loadConfig();
		
		// Check for Sodium
		isSodiumLoaded = FabricLoader.getInstance().isModLoaded("sodium");
		
		LOGGER.info("Optidum initialized!");
		LOGGER.info("Sodium compatibility: {}", isSodiumLoaded ? "ENABLED" : "DISABLED (Sodium not found)");
		LOGGER.info("Configuration loaded successfully");
		
		// Initialize optimization systems based on configuration
		initializeOptimizations();
	}
	
	private void initializeOptimizations() {
		var config = ConfigManager.getConfig();
		
		if (config.entityTickOptimization) {
			LOGGER.info("Entity tick optimization ENABLED");
		}
		
		if (config.chunkLoadingOptimization) {
			LOGGER.info("Chunk loading optimization ENABLED");
		}
		
		if (config.networkOptimization) {
			LOGGER.info("Network optimization ENABLED");
		}
		
		if (config.memoryOptimization) {
			LOGGER.info("Memory optimization ENABLED");
		}
		
		if (config.renderDistanceOptimization) {
			LOGGER.info("Render distance optimization ENABLED");
		}
		
		if (config.sodiumIntegration && isSodiumLoaded) {
			LOGGER.info("Sodium integration ENABLED");
		}
	}
}