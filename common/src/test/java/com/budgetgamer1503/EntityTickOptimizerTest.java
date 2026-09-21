package com.budgetgamer1503;

import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.optimization.ChunkOptimizer;
import com.budgetgamer1503.optimization.EntityTickOptimizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class EntityTickOptimizerTest {

    public static class DummyMonster {
        public int hurtTime = 0;
        public Object target = null;
        public int id = 100;
        public double x = 1000.0;
        public double z = 1000.0;

        public Object getTarget() { return target; }
        public int getId() { return id; }
        public double getX() { return x; }
        public double getZ() { return z; }
    }

    @BeforeEach
    public void setup() {
        ChunkOptimizer.clearAll();
        EntityTickOptimizer.resetGlobalTickCount();
        ConfigManager.getConfig().entityAiThrottle = true;
        ConfigManager.getConfig().entityAiReductionFactor = 3;
    }

    @Test
    public void testServerTickProgression() {
        assertEquals(0, EntityTickOptimizer.getGlobalTickCount());
        EntityTickOptimizer.onServerTick();
        assertEquals(1, EntityTickOptimizer.getGlobalTickCount());
    }

    @Test
    public void testThrottlingDistantMob() {
        DummyMonster mob = new DummyMonster();
        // At tick 0, (100 + 0) % 3 = 1 != 0 -> should throttle!
        assertTrue(EntityTickOptimizer.shouldThrottleMobAi(mob));

        // Advance ticks to when (100 + tick) % 3 == 0 (tick = 2)
        EntityTickOptimizer.onServerTick(); // tick 1 -> (101) % 3 = 2 != 0 -> throttle
        assertTrue(EntityTickOptimizer.shouldThrottleMobAi(mob));

        EntityTickOptimizer.onServerTick(); // tick 2 -> (102) % 3 = 0 -> DO NOT throttle
        assertFalse(EntityTickOptimizer.shouldThrottleMobAi(mob));
    }

    @Test
    public void testActiveTargetBypass() {
        DummyMonster mob = new DummyMonster();
        mob.target = new Object();
        // Has target -> should NOT throttle
        assertFalse(EntityTickOptimizer.shouldThrottleMobAi(mob));
    }

    @Test
    public void testHurtTimeBypass() {
        DummyMonster mob = new DummyMonster();
        mob.hurtTime = 10;
        // Mob hurt -> should NOT throttle
        assertFalse(EntityTickOptimizer.shouldThrottleMobAi(mob));
    }

    @Test
    public void testNearbyPlayerBypass() {
        DummyMonster mob = new DummyMonster();
        mob.x = 160.0;
        mob.z = 160.0;
        int chunkX = 10;
        int chunkZ = 10;

        // Register player right next to mob
        UUID playerId = UUID.randomUUID();
        ChunkOptimizer.updatePlayer(playerId, chunkX, chunkZ, 0.0f);

        // Player is nearby -> should NOT throttle
        assertFalse(EntityTickOptimizer.shouldThrottleMobAi(mob));

        ChunkOptimizer.clearPlayer(playerId);
    }
}
