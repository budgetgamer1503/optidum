package com.budgetgamer1503;

import com.budgetgamer1503.core.CompatibilityManager;
import com.budgetgamer1503.optimization.MemoryOptimizer;
import org.junit.jupiter.api.Test;

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
    public void testCompatibilityManagerInit() {
        assertDoesNotThrow(CompatibilityManager::init);
        assertFalse(CompatibilityManager.checkModLoaded("non_existent_mod_xyz"));
    }
}
