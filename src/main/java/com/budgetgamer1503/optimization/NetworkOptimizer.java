package com.budgetgamer1503.optimization;

import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkOptimizer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/NetworkOptimizer");
    private static long packetsProcessed = 0;
    private static long flushesDeferred = 0;

    public static boolean shouldDeferFlush(Packet<?> packet, boolean originalFlush) {
        if (!originalFlush) {
            return false;
        }

        OptidumConfig config = ConfigManager.getConfig();
        if (!config.networkOptimization || !config.packetBatching) {
            return false;
        }

        if (packet instanceof ClientboundKeepAlivePacket ||
            packet instanceof ServerboundKeepAlivePacket ||
            packet instanceof ClientboundSystemChatPacket ||
            packet instanceof ServerboundChatPacket) {
            return false;
        }

        packetsProcessed++;
        flushesDeferred++;
        return true;
    }

    public static void logStats() {
        OptidumConfig config = ConfigManager.getConfig();
        if (config.enableDebugLogging && packetsProcessed > 0 && packetsProcessed % 1000 == 0) {
            LOGGER.debug("Network flush consolidation: {} packets processed, {} flushes batched",
                packetsProcessed, flushesDeferred);
        }
    }
}