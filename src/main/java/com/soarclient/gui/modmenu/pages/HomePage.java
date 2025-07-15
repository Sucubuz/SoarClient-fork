package com.soarclient.gui.modmenu.pages;

import com.soarclient.Soar;
import com.soarclient.gui.api.SoarGui;
import com.soarclient.gui.api.page.Page;
import com.soarclient.gui.api.page.impl.RightLeftTransition;
import com.soarclient.management.color.api.ColorPalette;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Fonts;
import com.soarclient.skia.font.Icon;
import com.soarclient.utils.language.I18n;
import io.github.humbleui.skija.ClipMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;

public class HomePage extends Page {
    private static final float CARD_WIDTH = 300;
    private static final float CARD_HEIGHT = 120;
    private float cardX;
    private float cardY;

    public HomePage(SoarGui parent) {
        super(parent, "text.home", Icon.HOME, new RightLeftTransition(true));
    }

    @Override
    public void init() {
        super.init();
        cardX = x + 20;
        cardY = y + 90;
    }

    @Override
    public void draw(double mouseX, double mouseY) {
        super.draw(mouseX, mouseY);

        ColorPalette palette = Soar.getInstance().getColorManager().getPalette();
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        Skia.drawText(I18n.get("text.home"), x + 32, y + 30,
                palette.getOnSurface(), Fonts.getRegular(40));

        if (player != null) {
            Skia.drawRoundedRect(cardX, cardY, CARD_WIDTH, CARD_HEIGHT, 10, palette.getSurface());

            // head
            float avatarSize = 90;
            float avatarX = cardX + 20;
            float avatarY = cardY + (CARD_HEIGHT - avatarSize) / 2;

            Skia.drawRoundedRect(avatarX, avatarY, avatarSize, avatarSize, 8, palette.getSurfaceContainerHighest());

            // get and render skin
            SkinTextures skinTextures = player.getSkinTextures();
            if (skinTextures != null && skinTextures.texture() != null) {
                MinecraftClient mc = MinecraftClient.getInstance();
                int textureId = mc.getTextureManager().getTexture(skinTextures.texture()).getGlId();

                Skia.save();

                float skinWidth = 64;
                float skinHeight = 64;
                float headX = 8;
                float headY = 8;
                float headSize = 8;

                float scale = avatarSize / headSize;
                float drawX = avatarX - (headX * scale);
                float drawY = avatarY - (headY * scale);
                float totalWidth = skinWidth * scale;
                float totalHeight = skinHeight * scale;

                Skia.getCanvas().clipRRect(io.github.humbleui.types.RRect.makeXYWH(avatarX, avatarY, avatarSize, avatarSize, 8), ClipMode.INTERSECT, true);
                Skia.drawImage(textureId, drawX, drawY, totalWidth, totalHeight);

                Skia.restore();
            }

            // player name
            float textX = avatarX + avatarSize + 15;
            float textBaselineY = cardY + CARD_HEIGHT/2 - 33;
            String playerName = player.getName().getString();
            Skia.drawText(playerName, textX, textBaselineY, palette.getOnSurface(), Fonts.getRegular(30));
        }
    }
}
