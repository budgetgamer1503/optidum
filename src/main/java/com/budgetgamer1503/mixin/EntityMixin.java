package com.budgetgamer1503.mixin;

import com.budgetgamer1503.optimization.EntityTickOptimizer;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class EntityMixin {

    @Inject(method = "serverAiStep", at = @At("HEAD"), cancellable = true)
    private void optidum$throttleMobAi(CallbackInfo ci) {
        Mob self = (Mob) (Object) this;
        if (EntityTickOptimizer.shouldThrottleMobAi(self)) {
            ci.cancel();
        }
    }
}