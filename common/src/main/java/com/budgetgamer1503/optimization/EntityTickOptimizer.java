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

    private static final class EntityClassMetadata {
        final boolean isHostile;
        final boolean isAnimal;
        final boolean isAmbient;
        final Method getTargetMethod;
        final Field hurtTimeField;
        final Method getIdMethod;
        final Method getXMethod;
        final Method getZMethod;

        EntityClassMetadata(Class<?> clazz) {
            String name = clazz.getSimpleName().toLowerCase();
            this.isHostile = name.contains("monster") || name.contains("zombie") ||
                name.contains("skeleton") || name.contains("creeper") ||
                name.contains("spider") || name.contains("hostile") ||
                name.contains("pillager") || name.contains("raider");

            this.isAnimal = name.contains("animal") || name.contains("cow") ||
                name.contains("sheep") || name.contains("pig") ||
                name.contains("chicken") || name.contains("horse") ||
                name.contains("passive");

            this.isAmbient = name.contains("ambient") || name.contains("bat") ||
                name.contains("squid");

            this.getTargetMethod = findMethod(clazz, new String[]{"getTarget", "method_5968"});
            this.hurtTimeField = findField(clazz, new String[]{"hurtTime", "field_6235"});
            this.getIdMethod = findMethod(clazz, new String[]{"getId", "method_5628"});
            this.getXMethod = findPosMethod(clazz, new String[]{"getX", "method_23317"});
            this.getZMethod = findPosMethod(clazz, new String[]{"getZ", "method_23321"});
        }
    }

    private static final ClassValue<EntityClassMetadata> METADATA_CACHE = new ClassValue<>() {
        @Override
        protected EntityClassMetadata computeValue(Class<?> type) {
            return new EntityClassMetadata(type);
        }
    };

    private static Method findMethod(Class<?> clazz, String[] names) {
        for (Method m : clazz.getMethods()) {
            if (m.getParameterCount() == 0) {
                for (String name : names) {
                    if (m.getName().equals(name)) {
                        try {
                            m.setAccessible(true);
                        } catch (Exception ignored) {}
                        return m;
                    }
                }
            }
        }
        return null;
    }

    private static Method findPosMethod(Class<?> clazz, String[] names) {
        for (Method m : clazz.getMethods()) {
            if (m.getParameterCount() == 0 && (m.getReturnType() == double.class || m.getReturnType() == float.class)) {
                for (String name : names) {
                    if (m.getName().equals(name)) {
                        try {
                            m.setAccessible(true);
                        } catch (Exception ignored) {}
                        return m;
                    }
                }
            }
        }
        return null;
    }

    private static Field findField(Class<?> clazz, String[] names) {
        Class<?> curr = clazz;
        while (curr != null && curr != Object.class) {
            for (String name : names) {
                try {
                    Field f = curr.getDeclaredField(name);
                    f.setAccessible(true);
                    return f;
                } catch (NoSuchFieldException ignored) {}
            }
            curr = curr.getSuperclass();
        }
        return null;
    }

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
            EntityClassMetadata meta = METADATA_CACHE.get(mob.getClass());

            if (meta.getTargetMethod != null) {
                Object target = meta.getTargetMethod.invoke(mob);
                if (target != null) {
                    return false;
                }
            }

            if (meta.hurtTimeField != null) {
                int hurtTime = meta.hurtTimeField.getInt(mob);
                if (hurtTime > 0) {
                    return false;
                }
            }

            if (meta.isHostile && !config.optimizeHostileEntities) {
                return false;
            }
            if (meta.isAnimal && !config.optimizePassiveEntities) {
                return false;
            }
            if (meta.isAmbient && !config.optimizeAmbientEntities) {
                return false;
            }

            int distance = config.entityAiDistance;
            int factor = Math.max(2, config.entityAiReductionFactor);

            if (config.adaptiveThrottlingOnMemoryPressure && MemoryOptimizer.isHighPressureAlerted()) {
                distance = Math.max(16, (int) (distance * 0.75));
                factor += 1;
            }

            int chunkX = 0;
            int chunkZ = 0;
            if (meta.getXMethod != null) {
                double x = ((Number) meta.getXMethod.invoke(mob)).doubleValue();
                chunkX = ((int) Math.floor(x)) >> 4;
            }
            if (meta.getZMethod != null) {
                double z = ((Number) meta.getZMethod.invoke(mob)).doubleValue();
                chunkZ = ((int) Math.floor(z)) >> 4;
            }

            if (ChunkOptimizer.isPlayerNear(chunkX, chunkZ, distance)) {
                return false;
            }

            int entityId = (meta.getIdMethod != null) ? (int) meta.getIdMethod.invoke(mob) : mob.hashCode();
            return (entityId + globalTickCount) % factor != 0;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static long getGlobalTickCount() {
        return globalTickCount;
    }

    public static void resetGlobalTickCount() {
        globalTickCount = 0;
    }
}
