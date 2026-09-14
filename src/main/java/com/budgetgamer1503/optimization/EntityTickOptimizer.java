package com.budgetgamer1503.optimization;

import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityTickOptimizer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/EntityOptimizer");
    private static long globalTickCount = 0;

    public static void onServerTick() {
        globalTickCount++;
    }

    public static boolean shouldThrottleMobAi(Mob mob) {
        OptidumConfig config = ConfigManager.getConfig();
        if (!config.entityAiThrottle) {
            return false;
        }

        if (mob.getTarget() != null || mob.hurtTime > 0) {
            return false;
        }

        if (mob instanceof Monster && !config.optimizeHostileEntities) {
            return false;
        }
        if (mob instanceof Animal && !config.optimizePassiveEntities) {
            return false;
        }
        if (mob instanceof AmbientCreature && !config.optimizeAmbientEntities) {
            return false;
        }

        if (mob.level().hasNearbyAlivePlayer(mob.getX(), mob.getY(), mob.getZ(), config.entityAiDistance)) {
            return false;
        }

        int factor = Math.max(2, config.entityAiReductionFactor);
        return (mob.getId() + globalTickCount) % factor != 0;
    }

    public static void logOptimizationStats() {
        OptidumConfig config = ConfigManager.getConfig();
        if (config.enableDebugLogging && globalTickCount % 600 == 0) {
            LOGGER.debug("Entity AI optimizer active. Global tick: {}", globalTickCount);
        }
    }
}