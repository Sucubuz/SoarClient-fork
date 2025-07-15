package com.soarclient.mixin.mixins.minecraft.client;

import com.soarclient.management.mod.impl.hud.CPSDisplayMod;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.soarclient.Soar;
import com.soarclient.event.EventBus;
import com.soarclient.event.client.MouseScrollEvent;
import com.soarclient.management.mod.settings.impl.KeybindSetting;

import net.minecraft.client.Mouse;
import net.minecraft.client.util.InputUtil.Type;

@Mixin(Mouse.class)
public class MixinMouse {

    @Inject(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;onKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;)V", shift = At.Shift.AFTER))
    public void onPressed(long window, int button, int action, int mods, CallbackInfo ci) {

        for (KeybindSetting s : Soar.getInstance().getModManager().getKeybindSettings()) {

            if (s.getKey().equals(Type.MOUSE.createFromCode(button))) {

                if (action == GLFW.GLFW_PRESS) {
                    s.setPressed();
                }

                s.setKeyDown(true);
            }
        }
    }

    @Inject(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;setKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;Z)V", shift = At.Shift.AFTER, ordinal = 0))
    public void onReleased(long window, int button, int action, int mods, CallbackInfo ci) {
        for (KeybindSetting s : Soar.getInstance().getModManager().getKeybindSettings()) {
            if (s.getKey().equals(Type.MOUSE.createFromCode(button))) {
                s.setKeyDown(false);
            }
        }
    }

    @Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;setSelectedSlot(I)V", shift = At.Shift.BEFORE), cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {

        MouseScrollEvent event = new MouseScrollEvent(vertical);

        EventBus.getInstance().post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    public void onMouseButtonForCPS(long window, int button, int action, int mods, CallbackInfo ci) {
        CPSDisplayMod cpsDisplayMod = Soar.getInstance().getModManager().getMods()
            .stream()
            .filter(mod -> mod instanceof CPSDisplayMod)
            .map(mod -> (CPSDisplayMod) mod)
            .findFirst()
            .orElse(null);

        if (cpsDisplayMod != null && action == GLFW.GLFW_PRESS) {
            cpsDisplayMod.onMouseClick(button, true);
        }
    }
}


