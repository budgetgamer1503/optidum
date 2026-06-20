package com.budgetgamer1503.mixin;

import com.budgetgamer1503.optimization.EntityTickOptimizer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onEntityTick(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (EntityTickOptimizer.shouldSkipEntityTick(self)) {
            ci.cancel();
        }
    }
}