package com.budgetgamer1503;

import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.optimization.NetworkOptimizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NetworkOptimizerTest {

    static class DummyKeepAlivePacket {}
    static class DummySystemChatPacket {}
    static class DummyChunkDataPacket {}
    static class DummyEntityMovePacket {}

    @BeforeEach
    public void setup() {
        NetworkOptimizer.resetStats();
        ConfigManager.getConfig().networkOptimization = true;
        ConfigManager.getConfig().packetBatching = true;
    }

    @Test
    public void testDeferRegularPackets() {
        DummyChunkDataPacket chunkPacket = new DummyChunkDataPacket();
        DummyEntityMovePacket movePacket = new DummyEntityMovePacket();

        assertTrue(NetworkOptimizer.shouldDeferFlush(chunkPacket, true));
        assertTrue(NetworkOptimizer.shouldDeferFlush(movePacket, true));
        assertEquals(2, NetworkOptimizer.getPacketsProcessed());
        assertEquals(2, NetworkOptimizer.getFlushesDeferred());
    }

    @Test
    public void testBypassCriticalPackets() {
        DummyKeepAlivePacket keepAlive = new DummyKeepAlivePacket();
        DummySystemChatPacket chat = new DummySystemChatPacket();

        assertFalse(NetworkOptimizer.shouldDeferFlush(keepAlive, true));
        assertFalse(NetworkOptimizer.shouldDeferFlush(chat, true));
    }

    @Test
    public void testOriginalFlushFalse() {
        DummyChunkDataPacket chunkPacket = new DummyChunkDataPacket();
        assertFalse(NetworkOptimizer.shouldDeferFlush(chunkPacket, false));
    }
}
