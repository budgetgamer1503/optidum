package com.budgetgamer1503.v1211.mixin;

import com.budgetgamer1503.optimization.EntityTickOptimizer;
import com.budgetgamer1503.optimization.MemoryOptimizer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {
    "net.minecraft.server.MinecraftServer",
    "net.minecraft.class_3176"
})
public class MinecraftServerMixin {

    @Inject(
        method = {"tickServer", "tickChildren", "method_3748"},
        at = @At("HEAD"),
        remap = false,
        require = 0
    )
    private void optidum$onServerTick(CallbackInfo ci) {
        EntityTickOptimizer.onServerTick();
        MemoryOptimizer.periodicCheck();
    }
}
