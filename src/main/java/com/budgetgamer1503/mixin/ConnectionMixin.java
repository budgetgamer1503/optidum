package com.budgetgamer1503.mixin;

import com.budgetgamer1503.optimization.NetworkOptimizer;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Connection.class)
public class ConnectionMixin {
    
    // Unique ID per connection for tracking
    private final UUID optidum$connectionId = UUID.randomUUID();
    
    @Inject(method = "handleDisconnection", at = @At("HEAD"))
    private void onDisconnect(CallbackInfo ci) {
        NetworkOptimizer.cleanupConnection(optidum$connectionId);
    }
}