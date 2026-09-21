package com.budgetgamer1503.v1171.mixin;

import com.budgetgamer1503.optimization.ChunkOptimizer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {
    "net.minecraft.server.level.ServerChunkCache",
    "net.minecraft.server.world.ServerChunkManager",
    "net.minecraft.class_3215"
})
public class ServerChunkCacheMixin {

    @Inject(
        method = {"tick", "method_14154"},
        at = @At("HEAD"),
        remap = false,
        require = 0
    )
    private void optidum$onChunkCacheTick(CallbackInfo ci) {
        ChunkOptimizer.tick();
    }
}
