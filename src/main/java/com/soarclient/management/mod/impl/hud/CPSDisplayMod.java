package com.soarclient.management.mod.impl.hud;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import com.soarclient.event.EventBus;
import com.soarclient.event.client.ClientTickEvent;
import com.soarclient.event.client.RenderSkiaEvent;
import com.soarclient.management.mod.api.hud.SimpleHUDMod;
import com.soarclient.management.mod.settings.impl.BooleanSetting;
import com.soarclient.skia.font.Icon;

public class CPSDisplayMod extends SimpleHUDMod {

    private ArrayList<Long> leftPresses = new ArrayList<Long>();
    private ArrayList<Long> rightPresses = new ArrayList<Long>();

    private BooleanSetting rightClickSetting = new BooleanSetting("setting.rightclick",
        "setting.rightclick.description", Icon.MOUSE, this, true);

    public CPSDisplayMod() {
        super("mod.cpsdisplay.name", "mod.cpsdisplay.description", Icon.MOUSE);
    }

    public final EventBus.EventListener<RenderSkiaEvent> onRenderSkia = event -> {
        this.draw();
    };

    public final EventBus.EventListener<ClientTickEvent> onClientTick = event -> {
        leftPresses.removeIf(t -> System.currentTimeMillis() - t > 1000);
        rightPresses.removeIf(t -> System.currentTimeMillis() - t > 1000);
    };

    public void onMouseClick(int button, boolean pressed) {
        if (pressed) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                leftPresses.add(System.currentTimeMillis());
            }
            if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                rightPresses.add(System.currentTimeMillis());
            }
        }
    }

    @Override
    public String getText() {
        return (rightClickSetting.isEnabled() ?
            leftPresses.size() + " | " + rightPresses.size() :
            leftPresses.size()) + " CPS";
    }

    @Override
    public String getIcon() {
        return Icon.MOUSE;
    }
}
