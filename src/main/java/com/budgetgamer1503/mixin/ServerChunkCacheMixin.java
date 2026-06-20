package com.budgetgamer1503.mixin;

import com.budgetgamer1503.optimization.ChunkOptimizer;
import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin {
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void onChunkCacheTick(CallbackInfo ci) {
        ChunkOptimizer.logStats();
    }
}