package com.budgetgamer1503.client.mixin;

import com.budgetgamer1503.optimization.RenderDistanceManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {
    
    static {
        // Register the render distance applier callback
        RenderDistanceManager.setApplier(renderDistance -> {
            Minecraft client = Minecraft.getInstance();
            if (client != null && client.options != null) {
                client.options.renderDistance().set(renderDistance);
            }
        });
    }
    
    @Inject(method = "runTick", at = @At("HEAD"))
    private void onRenderFrame(boolean renderLevel, CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;
        if (client.options != null) {
            RenderDistanceManager.syncCurrentRenderDistance(client.options.renderDistance().get());
        }
        RenderDistanceManager.onFrame();
    }
}