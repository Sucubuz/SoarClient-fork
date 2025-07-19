package com.soarclient.management.mod.impl.hud;

import java.awt.Color;

import com.soarclient.Soar;
import com.soarclient.event.EventBus;
import com.soarclient.event.client.RenderSkiaEvent;
import com.soarclient.management.color.api.ColorPalette;
import com.soarclient.management.mod.api.hud.HUDMod;
import com.soarclient.management.mod.settings.impl.StringSetting;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Fonts;
import com.soarclient.skia.font.Icon;

import io.github.humbleui.skija.FontMetrics;
import io.github.humbleui.types.Rect;

public class WatermarkMod extends HUDMod {

    private static WatermarkMod instance;

    private StringSetting textSetting = new StringSetting("setting.text",
        "setting.text.description", Icon.TEXT_FIELDS, this, "Soar Client");

    public WatermarkMod() {
        super("mod.watermark.name", "mod.watermark.description", Icon.BRANDING_WATERMARK);
        instance = this;
    }

    public static WatermarkMod getInstance() {
        return instance;
    }

    public final EventBus.EventListener<RenderSkiaEvent> onRenderSkia = event -> {
        this.draw();
    };

    private void draw() {
        try {
            this.begin();
            drawTextMode();
        } catch (Exception e) {
            System.err.println("Error in WatermarkMod.draw(): " + e.getMessage());
            e.printStackTrace();
            position.setSize(100, 20);
        } finally {
            try {
                this.finish();
            } catch (Exception e) {
                System.err.println("Error in finish(): " + e.getMessage());
            }
        }
    }

    private void drawTextMode() {
        String text = textSetting.getValue();
        float fontSize = 24;

        Rect textBounds = Skia.getTextBounds(text, Fonts.getMedium(fontSize));
        FontMetrics metrics = Fonts.getMedium(fontSize).getMetrics();

        float width = textBounds.getWidth();
        float height = fontSize;
        float textCenterY = (metrics.getAscent() - metrics.getDescent()) / 2 - metrics.getAscent();

        Color gradientColor = getAnimatedColor();

        Skia.drawText(text, getX() + 1, getY() + (height / 2) - textCenterY + 1,
            new Color(0, 0, 0, 100), Fonts.getMedium(fontSize));

        Skia.drawText(text, getX(), getY() + (height / 2) - textCenterY,
            gradientColor, Fonts.getMedium(fontSize));

        position.setSize(width, height);
    }

    private Color getAnimatedColor() {
        try {
            ColorPalette palette = Soar.getInstance().getColorManager().getPalette();

            if (palette == null) {
                return Color.WHITE;
            }

            long currentTime = System.nanoTime();
            double speed = 0.000000002; // speed
            double cycle = (currentTime * speed) % (2 * Math.PI);

            Color color1 = palette.getPrimary();
            Color color2 = palette.getSecondary();
            Color color3 = palette.getTertiary();

            if (color1 == null) color1 = Color.WHITE;
            if (color2 == null) color2 = Color.LIGHT_GRAY;
            if (color3 == null) color3 = Color.GRAY;

            double normalizedCycle = (cycle / (2 * Math.PI)) * 3;

            Color resultColor;
            if (normalizedCycle < 1) {
                float factor = (float) normalizedCycle;
                resultColor = blendColors(color1, color2, factor);
            } else if (normalizedCycle < 2) {
                float factor = (float) (normalizedCycle - 1);
                resultColor = blendColors(color2, color3, factor);
            } else {
                float factor = (float) (normalizedCycle - 2);
                resultColor = blendColors(color3, color1, factor);
            }

            return resultColor;

        } catch (Exception e) {
            System.err.println("Error in getAnimatedColor: " + e.getMessage());
            return Color.WHITE;
        }
    }

    private Color blendColors(Color color1, Color color2, float factor) {
        int red = (int) (color1.getRed() * (1 - factor) + color2.getRed() * factor);
        int green = (int) (color1.getGreen() * (1 - factor) + color2.getGreen() * factor);
        int blue = (int) (color1.getBlue() * (1 - factor) + color2.getBlue() * factor);
        int alpha = (int) (color1.getAlpha() * (1 - factor) + color2.getAlpha() * factor);

        return new Color(red, green, blue, alpha);
    }

    @Override
    public float getRadius() {
        return 0;
    }
}
