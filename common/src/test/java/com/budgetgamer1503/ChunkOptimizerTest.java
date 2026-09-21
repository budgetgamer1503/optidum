package com.budgetgamer1503;

import com.budgetgamer1503.optimization.ChunkOptimizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ChunkOptimizerTest {

    @BeforeEach
    public void setup() {
        ChunkOptimizer.clearAll();
    }

    @Test
    public void testAngleNormalization() {
        assertEquals(0.0, ChunkOptimizer.normalizeAngle(0.0), 0.001);
        assertEquals(90.0, ChunkOptimizer.normalizeAngle(90.0), 0.001);
        assertEquals(-90.0, ChunkOptimizer.normalizeAngle(270.0), 0.001);
        assertEquals(-180.0, ChunkOptimizer.normalizeAngle(180.0), 0.001);
        assertEquals(0.0, ChunkOptimizer.normalizeAngle(360.0), 0.001);
        assertEquals(-180.0, ChunkOptimizer.normalizeAngle(-180.0), 0.001);
    }

    @Test
    public void testChunkPacking() {
        long packed = ChunkOptimizer.packChunkPos(15, -25);
        int x = (int) packed;
        int z = (int) (packed >> 32);
        assertEquals(15, x);
        assertEquals(-25, z);
    }

    @Test
    public void testPlayerTrackingAndProximity() {
        UUID playerId = UUID.randomUUID();
        ChunkOptimizer.updatePlayer(playerId, 10, 10, 0.0f);

        assertEquals(1, ChunkOptimizer.getTrackedPlayerCount());
        assertTrue(ChunkOptimizer.isPlayerNear(10, 10, 32));
        assertTrue(ChunkOptimizer.isPlayerNear(11, 10, 32));
        assertFalse(ChunkOptimizer.isPlayerNear(50, 50, 32));

        ChunkOptimizer.clearPlayer(playerId);
        assertEquals(0, ChunkOptimizer.getTrackedPlayerCount());
        assertFalse(ChunkOptimizer.isPlayerNear(10, 10, 32));
    }

    @Test
    public void testViewDirectionPrioritization() {
        UUID playerId = UUID.randomUUID();
        // Yaw 0.0 points towards +Z (in Minecraft coordinate convention)
        ChunkOptimizer.updatePlayer(playerId, 0, 0, 0.0f);

        // Chunks in front should be in view direction
        assertTrue(ChunkOptimizer.isChunkInViewDirection(playerId, 0, 0));
        assertTrue(ChunkOptimizer.isChunkInViewDirection(playerId, 5, 0));
    }
}
