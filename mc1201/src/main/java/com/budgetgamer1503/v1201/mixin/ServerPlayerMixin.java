package com.budgetgamer1503.v1201.mixin;

import com.budgetgamer1503.optimization.ChunkOptimizer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.util.UUID;

@Pseudo
@Mixin(targets = {
    "net.minecraft.server.level.ServerPlayer",
    "net.minecraft.server.network.ServerPlayerEntity",
    "net.minecraft.class_3222"
})
public class ServerPlayerMixin {

    private static Method getUuidMethod = null;
    private static Method getXMethod = null;
    private static Method getZMethod = null;
    private static Method getYawMethod = null;
    private static boolean methodsLookedUp = false;

    @Inject(
        method = {"tick", "playerTick", "method_14226"},
        at = @At("HEAD"),
        remap = false,
        require = 0
    )
    private void optidum$onPlayerTick(CallbackInfo ci) {
        try {
            if (!methodsLookedUp) {
                methodsLookedUp = true;
                for (Method m : getClass().getMethods()) {
                    if (m.getParameterCount() == 0) {
                        String name = m.getName();
                        if (name.equals("getUUID") || name.equals("method_5667")) {
                            getUuidMethod = m;
                        } else if (name.equals("getBlockX") || name.equals("getX") || name.equals("method_23317")) {
                            if (getXMethod == null) getXMethod = m;
                        } else if (name.equals("getBlockZ") || name.equals("getZ") || name.equals("method_23321")) {
                            if (getZMethod == null) getZMethod = m;
                        } else if (name.equals("getYRot") || name.equals("getYaw") || name.equals("method_36454")) {
                            getYawMethod = m;
                        }
                    }
                }
            }

            if (getUuidMethod != null) {
                UUID uuid = (UUID) getUuidMethod.invoke(this);
                if (uuid != null) {
                    int blockX = getXMethod != null ? ((Number) getXMethod.invoke(this)).intValue() : 0;
                    int blockZ = getZMethod != null ? ((Number) getZMethod.invoke(this)).intValue() : 0;
                    float yaw = getYawMethod != null ? ((Number) getYawMethod.invoke(this)).floatValue() : 0.0f;
                    ChunkOptimizer.updatePlayer(uuid, blockX >> 4, blockZ >> 4, yaw);
                }
            }
        } catch (Throwable ignored) {}
    }
}
