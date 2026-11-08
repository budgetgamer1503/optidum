package com.budgetgamer1503;

import com.budgetgamer1503.core.CompatibilityManager;
import com.budgetgamer1503.optimization.MemoryOptimizer;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class MemoryOptimizerTest {

    @Test
    public void testPeriodicCheckDoesNotThrow() {
        assertDoesNotThrow(MemoryOptimizer::periodicCheck);
    }

    @Test
    public void testDimensionChangeDoesNotThrow() {
        assertDoesNotThrow(MemoryOptimizer::onDimensionChange);
    }

    @Test
    public void testTrimCachesInvokesCallback() {
        AtomicBoolean invoked = new AtomicBoolean(false);
        MemoryOptimizer.registerTrimCallback(level -> {
            if (level == MemoryOptimizer.MemoryTrimLevel.DIMENSION_CHANGE) {
                invoked.set(true);
            }
        });

        MemoryOptimizer.trimCaches(MemoryOptimizer.MemoryTrimLevel.DIMENSION_CHANGE);
        assertTrue(invoked.get());
    }

    @Test
    public void testHighPressureTestingHook() {
        MemoryOptimizer.setHighPressureAlertedForTesting(true);
        assertTrue(MemoryOptimizer.isHighPressureAlerted());
        MemoryOptimizer.setHighPressureAlertedForTesting(false);
        assertFalse(MemoryOptimizer.isHighPressureAlerted());
    }

    @Test
    public void testCompatibilityManagerInit() {
        assertDoesNotThrow(CompatibilityManager::init);
        assertFalse(CompatibilityManager.checkModLoaded("non_existent_mod_xyz"));
    }
}
