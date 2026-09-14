package com.budgetgamer1503.mixin;

import com.budgetgamer1503.optimization.EntityTickOptimizer;
import com.budgetgamer1503.optimization.MemoryOptimizer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "tickServer", at = @At("HEAD"))
    private void optidum$onServerTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        EntityTickOptimizer.onServerTick();
        MemoryOptimizer.periodicCheck();
    }
}