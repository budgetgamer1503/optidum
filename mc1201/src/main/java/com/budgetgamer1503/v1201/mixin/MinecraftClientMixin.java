package com.budgetgamer1503.v1201.mixin;

import com.budgetgamer1503.optimization.MemoryOptimizer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {
    "net.minecraft.client.Minecraft",
    "net.minecraft.client.MinecraftClient",
    "net.minecraft.class_310"
})
public class MinecraftClientMixin {

    @Inject(
        method = {"setLevel", "setWorld", "joinWorld", "disconnect", "method_18099"},
        at = @At("HEAD"),
        remap = false,
        require = 0
    )
    private void optidum$onLevelChange(CallbackInfo ci) {
        MemoryOptimizer.onDimensionChange();
    }
}
