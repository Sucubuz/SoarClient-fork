package com.soarclient.management.mod.impl.player;

import org.lwjgl.glfw.GLFW;

import com.soarclient.event.EventBus;
import com.soarclient.event.client.ClientTickEvent;
import com.soarclient.management.mod.Mod;
import com.soarclient.management.mod.ModCategory;
import com.soarclient.management.mod.settings.impl.KeybindSetting;
import com.soarclient.management.mod.settings.impl.StringSetting;
import com.soarclient.skia.font.Icon;

import net.minecraft.client.util.InputUtil;

public class AutoTextMod extends Mod {

    private KeybindSetting text1KeybindSetting = new KeybindSetting("setting.text1key", "setting.text1key.description",
        Icon.KEYBOARD, this, InputUtil.UNKNOWN_KEY);
    private StringSetting text1Setting = new StringSetting("setting.text1", "setting.text1.description",
        Icon.TEXT_FIELDS, this, "");

    private KeybindSetting text2KeybindSetting = new KeybindSetting("setting.text2key", "setting.text2key.description",
        Icon.KEYBOARD, this, InputUtil.UNKNOWN_KEY);
    private StringSetting text2Setting = new StringSetting("setting.text2", "setting.text2.description",
        Icon.TEXT_FIELDS, this, "");

    private KeybindSetting text3KeybindSetting = new KeybindSetting("setting.text3key", "setting.text3key.description",
        Icon.KEYBOARD, this, InputUtil.UNKNOWN_KEY);
    private StringSetting text3Setting = new StringSetting("setting.text3", "setting.text3.description",
        Icon.TEXT_FIELDS, this, "");

    public AutoTextMod() {
        super("mod.autotext.name", "mod.autotext.description", Icon.TEXT_FIELDS, ModCategory.PLAYER);
    }

    public final EventBus.EventListener<ClientTickEvent> onClientTick = event -> {

        if (client.player == null) {
            return;
        }

        if (text1KeybindSetting.isPressed() && !text1Setting.getValue().isEmpty()) {
            client.player.networkHandler.sendChatMessage(text1Setting.getValue());
        }

        if (text2KeybindSetting.isPressed() && !text2Setting.getValue().isEmpty()) {
            client.player.networkHandler.sendChatMessage(text2Setting.getValue());
        }

        if (text3KeybindSetting.isPressed() && !text3Setting.getValue().isEmpty()) {
            client.player.networkHandler.sendChatMessage(text3Setting.getValue());
        }
    };
}
