package com.budgetgamer1503.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CompatibilityManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/Compat");
    private static boolean initialized = false;

    private static boolean sodiumLoaded = false;
    private static boolean titaniumLoaded = false;
    private static boolean dynamicFpsLoaded = false;
    private static boolean modMenuLoaded = false;
    private static boolean entityCullingLoaded = false;
    private static boolean moreCullingLoaded = false;
    private static boolean ferriteCoreLoaded = false;
    private static boolean kryptonLoaded = false;
    private static boolean c2meLoaded = false;

    private CompatibilityManager() {}

    public static synchronized void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        sodiumLoaded = checkModLoaded("sodium");
        titaniumLoaded = checkModLoaded("titanium");
        dynamicFpsLoaded = checkModLoaded("dynamic_fps");
        modMenuLoaded = checkModLoaded("modmenu");
        entityCullingLoaded = checkModLoaded("entityculling");
        moreCullingLoaded = checkModLoaded("moreculling");
        ferriteCoreLoaded = checkModLoaded("ferritecore");
        kryptonLoaded = checkModLoaded("krypton");
        c2meLoaded = checkModLoaded("c2me");

        LOGGER.info("[Optidum] Compatibility layer initialized.");
        LOGGER.info("[Optidum] Companion status: Sodium={}, Titanium={}, DynamicFPS={}, ModMenu={}",
            sodiumLoaded, titaniumLoaded, dynamicFpsLoaded, modMenuLoaded);

        if (titaniumLoaded) {
            LOGGER.info("[Optidum] Titanium detected: Seamless synergy active. Titanium manages frustum culling, Optidum manages AI, chunks & network.");
        }
        if (dynamicFpsLoaded) {
            LOGGER.info("[Optidum] Dynamic FPS detected: Optidum yields unfocused framerate control to Dynamic FPS.");
        }
        if (kryptonLoaded) {
            LOGGER.info("[Optidum] Krypton detected: Netty flush consolidation operating in harmony with Krypton event loop.");
        }
        if (c2meLoaded) {
            LOGGER.info("[Optidum] C2ME detected: Directional chunk prioritization synergizing with asynchronous chunk generation.");
        }
    }

    public static boolean isSodiumLoaded() {
        return sodiumLoaded;
    }

    public static boolean isTitaniumLoaded() {
        return titaniumLoaded;
    }

    public static boolean isDynamicFpsLoaded() {
        return dynamicFpsLoaded;
    }

    public static boolean isModMenuLoaded() {
        return modMenuLoaded;
    }

    public static boolean isEntityCullingLoaded() {
        return entityCullingLoaded;
    }

    public static boolean isMoreCullingLoaded() {
        return moreCullingLoaded;
    }

    public static boolean isFerriteCoreLoaded() {
        return ferriteCoreLoaded;
    }

    public static boolean isKryptonLoaded() {
        return kryptonLoaded;
    }

    public static boolean isC2meLoaded() {
        return c2meLoaded;
    }

    public static boolean checkModLoaded(String modId) {
        try {
            Class<?> flClass = Class.forName("net.fabricmc.loader.api.FabricLoader");
            Object instance = flClass.getMethod("getInstance").invoke(null);
            return (boolean) flClass.getMethod("isModLoaded", String.class).invoke(instance, modId);
        } catch (Throwable ignored) {
            return false;
        }
    }
}
