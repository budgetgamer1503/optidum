package com.budgetgamer1503.mixin;

import com.budgetgamer1503.optimization.NetworkOptimizer;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Connection.class)
public class ConnectionMixin {

    @ModifyVariable(
        method = "send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;Z)V",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private boolean optidum$optimizeFlush(boolean flush, Packet<?> packet, ChannelFutureListener listener) {
        if (NetworkOptimizer.shouldDeferFlush(packet, flush)) {
            return false;
        }
        return flush;
    }
}