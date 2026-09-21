package com.budgetgamer1503.optimization;

import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkOptimizer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/NetworkOptimizer");
    private static long packetsProcessed = 0;
    private static long flushesDeferred = 0;

    public static boolean shouldDeferFlush(Object packet, boolean originalFlush) {
        if (!originalFlush || packet == null) {
            return false;
        }

        OptidumConfig config = ConfigManager.getConfig();
        if (!config.networkOptimization || !config.packetBatching) {
            return false;
        }

        String simpleName = packet.getClass().getSimpleName();
        if (simpleName.contains("KeepAlive") ||
            simpleName.contains("Chat") ||
            simpleName.contains("CustomPayload") ||
            simpleName.contains("Login") ||
            simpleName.contains("Handshake") ||
            simpleName.contains("Disconnect")) {
            return false;
        }

        packetsProcessed++;
        flushesDeferred++;
        return true;
    }

    public static void logStats() {
        OptidumConfig config = ConfigManager.getConfig();
        if (config.enableDebugLogging && packetsProcessed > 0 && packetsProcessed % 1000 == 0) {
            LOGGER.debug("[Optidum] Network flush consolidation: {} packets processed, {} flushes batched",
                packetsProcessed, flushesDeferred);
        }
    }

    public static long getPacketsProcessed() {
        return packetsProcessed;
    }

    public static long getFlushesDeferred() {
        return flushesDeferred;
    }

    public static void resetStats() {
        packetsProcessed = 0;
        flushesDeferred = 0;
    }
}
