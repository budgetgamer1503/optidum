package com.budgetgamer1503.v1194.mixin;

import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import com.budgetgamer1503.core.CompatibilityManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;

@Pseudo
@Mixin(targets = {
    "com.mojang.blaze3d.platform.FramerateLimitTracker",
    "net.minecraft.client.Minecraft",
    "net.minecraft.client.MinecraftClient",
    "net.minecraft.class_310"
})
public class FramerateLimitTrackerMixin {

    private static Method isFocusedMethod = null;
    private static Method isIconifiedMethod = null;
    private static boolean windowMethodsLookedUp = false;

    @Inject(
        method = {"getFramerateLimit", "method_1598", "method_53528"},
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 0
    )
    private void optidum$throttleUnfocusedFps(CallbackInfoReturnable<Integer> cir) {
        if (CompatibilityManager.isDynamicFpsLoaded()) {
            return;
        }

        OptidumConfig config = ConfigManager.getConfig();
        if (!config.dynamicFpsEnabled) {
            return;
        }

        try {
            Class<?> mcClass = null;
            try {
                mcClass = Class.forName("net.minecraft.client.Minecraft");
            } catch (ClassNotFoundException e) {
                try {
                    mcClass = Class.forName("net.minecraft.client.MinecraftClient");
                } catch (ClassNotFoundException ignored) {}
            }

            if (mcClass != null) {
                Object mc = mcClass.getMethod("getInstance").invoke(null);
                if (mc != null) {
                    Method getWindow = null;
                    try {
                        getWindow = mc.getClass().getMethod("getWindow");
                    } catch (NoSuchMethodException e) {
                        try {
                            getWindow = mc.getClass().getMethod("method_22683");
                        } catch (NoSuchMethodException ignored) {}
                    }

                    if (getWindow != null) {
                        Object window = getWindow.invoke(mc);
                        if (window != null) {
                            if (!windowMethodsLookedUp) {
                                windowMethodsLookedUp = true;
                                for (Method m : window.getClass().getMethods()) {
                                    if (m.getParameterCount() == 0 && m.getReturnType() == boolean.class) {
                                        if (m.getName().equals("isFocused") || m.getName().equals("method_4490")) {
                                            isFocusedMethod = m;
                                        } else if (m.getName().equals("isIconified") || m.getName().equals("isMinimized")) {
                                            isIconifiedMethod = m;
                                        }
                                    }
                                }
                            }

                            boolean focused = isFocusedMethod != null ? (boolean) isFocusedMethod.invoke(window) : true;
                            boolean iconified = isIconifiedMethod != null ? (boolean) isIconifiedMethod.invoke(window) : false;

                            if (!focused || iconified) {
                                cir.setReturnValue(config.unfocusedFpsLimit);
                            }
                        }
                    }
                }
            }
        } catch (Throwable ignored) {}
    }
}
