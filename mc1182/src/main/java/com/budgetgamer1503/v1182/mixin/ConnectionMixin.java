package com.budgetgamer1503.v1182.mixin;

import com.budgetgamer1503.optimization.NetworkOptimizer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = {
    "net.minecraft.network.Connection",
    "net.minecraft.network.ClientConnection",
    "net.minecraft.class_2535"
})
public class ConnectionMixin {

    @ModifyVariable(
        method = {
            "send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;Z)V",
            "send(Lnet/minecraft/network/Packet;Lio/netty/channel/ChannelFutureListener;Z)V",
            "method_10743(Lnet/minecraft/class_2596;Lio/netty/channel/ChannelFutureListener;Z)V"
        },
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0,
        remap = false,
        require = 0
    )
    private boolean optidum$optimizeFlush(boolean flush, Object packet) {
        if (NetworkOptimizer.shouldDeferFlush(packet, flush)) {
            return false;
        }
        return flush;
    }
}
