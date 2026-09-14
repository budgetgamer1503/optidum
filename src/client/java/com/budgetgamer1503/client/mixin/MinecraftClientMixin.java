package com.budgetgamer1503.client.mixin;

import com.budgetgamer1503.optimization.MemoryOptimizer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {

    @Inject(method = "setLevel", at = @At("HEAD"))
    private void optidum$onLevelChange(ClientLevel level, CallbackInfo ci) {
        MemoryOptimizer.onDimensionChange();
    }
}