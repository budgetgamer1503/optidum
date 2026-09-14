package com.budgetgamer1503;

import com.budgetgamer1503.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Optidum implements ModInitializer {
	public static final String MOD_ID = "optidum";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean isSodiumLoaded = false;

	@Override
	public void onInitialize() {
		ConfigManager.loadConfig();
		isSodiumLoaded = FabricLoader.getInstance().isModLoaded("sodium");

		LOGGER.info("Optidum initialized!");
		LOGGER.info("Sodium compatibility: {}", isSodiumLoaded ? "ENABLED" : "DISABLED (Sodium not found)");
		LOGGER.info("Configuration loaded successfully");

		initializeOptimizations();
	}

	private void initializeOptimizations() {
		var config = ConfigManager.getConfig();

		if (config.entityAiThrottle) {
			LOGGER.info("Entity AI optimization ENABLED");
		}
		if (config.chunkLoadingOptimization) {
			LOGGER.info("Chunk loading optimization ENABLED");
		}
		if (config.networkOptimization) {
			LOGGER.info("Network packet batching ENABLED");
		}
		if (config.memoryOptimization) {
			LOGGER.info("Memory optimization ENABLED");
		}
		if (config.dynamicFpsEnabled) {
			LOGGER.info("Dynamic FPS power saver ENABLED");
		}
		if (config.sodiumIntegration && isSodiumLoaded) {
			LOGGER.info("Sodium integration ENABLED");
		}
	}
}