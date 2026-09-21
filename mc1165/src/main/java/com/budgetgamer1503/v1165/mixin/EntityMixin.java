package com.budgetgamer1503.v1165.mixin;

import com.budgetgamer1503.optimization.EntityTickOptimizer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {
    "net.minecraft.world.entity.Mob",
    "net.minecraft.entity.mob.MobEntity",
    "net.minecraft.class_1308"
})
public class EntityMixin {

    @Inject(
        method = {"serverAiStep", "mobTick", "method_5973"},
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 0
    )
    private void optidum$throttleMobAi(CallbackInfo ci) {
        if (EntityTickOptimizer.shouldThrottleMobAi(this)) {
            ci.cancel();
        }
    }
}
