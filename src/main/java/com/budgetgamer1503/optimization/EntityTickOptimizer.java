package com.budgetgamer1503.optimization;

import com.budgetgamer1503.Optidum;
import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityTickOptimizer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/EntityOptimizer");
    private static final OptidumConfig config = ConfigManager.getConfig();
    
    // Track tick counts for each entity
    private static long globalTickCount = 0;
    
    public static void onServerTick() {
        globalTickCount++;
    }
    
    public static boolean shouldSkipEntityTick(Entity entity) {
        if (!config.entityTickOptimization) {
            return false;
        }
        
        // Never skip player ticks
        if (entity instanceof Player) {
            return false;
        }
        
        // Check entity type optimizations
        if (entity instanceof Monster && !config.optimizeHostileEntities) {
            return false;
        }
        if (entity instanceof Animal && !config.optimizePassiveEntities) {
            return false;
        }
        if (entity instanceof AmbientCreature && !config.optimizeAmbientEntities) {
            return false;
        }
        
        // Calculate distance from nearest player
        double distanceSq = getDistanceToNearestPlayerSquared(entity);
        int distanceThreshold = config.entityTickDistance * config.entityTickDistance;
        
        // Skip ticks based on distance
        if (distanceSq > distanceThreshold) {
            // Use reduction factor to skip ticks
            int reductionFactor = config.entityTickReductionFactor;
            if (reductionFactor > 1) {
                long entityHash = entity.getId();
                return (entityHash + globalTickCount) % reductionFactor != 0;
            }
        }
        
        return false;
    }
    
    private static double getDistanceToNearestPlayerSquared(Entity entity) {
        // In a real implementation, we would find the nearest player
        // For now, return a placeholder value
        // This would be implemented in the mixin with actual player distance calculation
        return 0.0;
    }
    
    public static void logOptimizationStats() {
        if (config.enableDebugLogging) {
            LOGGER.debug("Entity tick optimizer active. Global tick: {}", globalTickCount);
        }
    }
}