package com.budgetgamer1503.optimization;

import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EntityTickOptimizer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/EntityOptimizer");
    private static long globalTickCount = 0;

    private static Method getTargetMethod = null;
    private static boolean getTargetLookedUp = false;

    private static Field hurtTimeField = null;
    private static boolean hurtTimeLookedUp = false;

    private static Method getIdMethod = null;
    private static boolean getIdLookedUp = false;

    private static Method getXMethod = null;
    private static Method getZMethod = null;
    private static boolean posMethodsLookedUp = false;

    public static void onServerTick() {
        globalTickCount++;
    }

    public static boolean shouldThrottleMobAi(Object mob) {
        if (mob == null) {
            return false;
        }

        OptidumConfig config = ConfigManager.getConfig();
        if (!config.entityAiThrottle) {
            return false;
        }

        try {
            if (hasActiveTarget(mob)) {
                return false;
            }

            if (isEntityHurt(mob)) {
                return false;
            }

            String className = mob.getClass().getSimpleName().toLowerCase();

            boolean isHostile = className.contains("monster") || className.contains("zombie") ||
                className.contains("skeleton") || className.contains("creeper") ||
                className.contains("spider") || className.contains("hostile") ||
                className.contains("pillager") || className.contains("raider");

            boolean isAnimal = className.contains("animal") || className.contains("cow") ||
                className.contains("sheep") || className.contains("pig") ||
                className.contains("chicken") || className.contains("horse") ||
                className.contains("passive");

            boolean isAmbient = className.contains("ambient") || className.contains("bat") ||
                className.contains("squid");

            if (isHostile && !config.optimizeHostileEntities) {
                return false;
            }
            if (isAnimal && !config.optimizePassiveEntities) {
                return false;
            }
            if (isAmbient && !config.optimizeAmbientEntities) {
                return false;
            }

            int chunkX = getEntityChunkX(mob);
            int chunkZ = getEntityChunkZ(mob);
            if (ChunkOptimizer.isPlayerNear(chunkX, chunkZ, config.entityAiDistance)) {
                return false;
            }

            int entityId = getEntityId(mob);
            int factor = Math.max(2, config.entityAiReductionFactor);
            return (entityId + globalTickCount) % factor != 0;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean hasActiveTarget(Object mob) {
        try {
            if (getTargetMethod == null) {
                for (Method m : mob.getClass().getMethods()) {
                    if (m.getName().equals("getTarget") || m.getName().equals("method_5968")) {
                        if (m.getParameterCount() == 0) {
                            m.setAccessible(true);
                            getTargetMethod = m;
                            break;
                        }
                    }
                }
            }
            if (getTargetMethod != null) {
                return getTargetMethod.invoke(mob) != null;
            }
        } catch (Throwable ignored) {}
        return false;
    }

    private static boolean isEntityHurt(Object mob) {
        try {
            if (hurtTimeField == null) {
                Class<?> curr = mob.getClass();
                while (curr != null && curr != Object.class) {
                    try {
                        Field f = curr.getDeclaredField("hurtTime");
                        f.setAccessible(true);
                        hurtTimeField = f;
                        break;
                    } catch (NoSuchFieldException e) {
                        try {
                            Field f = curr.getDeclaredField("field_6235");
                            f.setAccessible(true);
                            hurtTimeField = f;
                            break;
                        } catch (NoSuchFieldException ignored) {}
                    }
                    curr = curr.getSuperclass();
                }
            }
            if (hurtTimeField != null) {
                return hurtTimeField.getInt(mob) > 0;
            }
        } catch (Throwable ignored) {}
        return false;
    }

    private static int getEntityId(Object mob) {
        try {
            if (getIdMethod == null) {
                for (Method m : mob.getClass().getMethods()) {
                    if (m.getName().equals("getId") || m.getName().equals("method_5628")) {
                        if (m.getParameterCount() == 0 && m.getReturnType() == int.class) {
                            m.setAccessible(true);
                            getIdMethod = m;
                            break;
                        }
                    }
                }
            }
            if (getIdMethod != null) {
                return (int) getIdMethod.invoke(mob);
            }
        } catch (Throwable ignored) {}
        return mob.hashCode();
    }

    private static int getEntityChunkX(Object mob) {
        try {
            ensurePosMethods(mob);
            if (getXMethod != null) {
                double x = ((Number) getXMethod.invoke(mob)).doubleValue();
                return ((int) Math.floor(x)) >> 4;
            }
        } catch (Throwable ignored) {}
        return 0;
    }

    private static int getEntityChunkZ(Object mob) {
        try {
            ensurePosMethods(mob);
            if (getZMethod != null) {
                double z = ((Number) getZMethod.invoke(mob)).doubleValue();
                return ((int) Math.floor(z)) >> 4;
            }
        } catch (Throwable ignored) {}
        return 0;
    }

    private static void ensurePosMethods(Object mob) {
        if (getXMethod == null || getZMethod == null) {
            for (Method m : mob.getClass().getMethods()) {
                if (m.getParameterCount() == 0 && (m.getReturnType() == double.class || m.getReturnType() == float.class)) {
                    if (m.getName().equals("getX") || m.getName().equals("method_23317")) {
                        m.setAccessible(true);
                        getXMethod = m;
                    } else if (m.getName().equals("getZ") || m.getName().equals("method_23321")) {
                        m.setAccessible(true);
                        getZMethod = m;
                    }
                }
            }
        }
    }

    public static long getGlobalTickCount() {
        return globalTickCount;
    }

    public static void resetGlobalTickCount() {
        globalTickCount = 0;
    }
}
