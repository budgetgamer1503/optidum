package com.budgetgamer1503.client.mixin;

import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import com.mojang.blaze3d.platform.FramerateLimitTracker;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FramerateLimitTracker.class)
public class FramerateLimitTrackerMixin {

    @Inject(method = "getFramerateLimit", at = @At("HEAD"), cancellable = true)
    private void optidum$throttleUnfocusedFps(CallbackInfoReturnable<Integer> cir) {
        if (FabricLoader.getInstance().isModLoaded("dynamic_fps")) {
            return;
        }

        OptidumConfig config = ConfigManager.getConfig();
        if (!config.dynamicFpsEnabled) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.getWindow() != null) {
            if (!mc.getWindow().isFocused() || mc.getWindow().isIconified()) {
                cir.setReturnValue(config.unfocusedFpsLimit);
            }
        }
    }
}
