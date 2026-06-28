package net.minecraft.client.gui;

import net.minecraft.util.ResourceLocation;

public class AbstractGui extends Gui {
    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("textures/gui/options_background.png");

    public static void fill(int left, int top, int right, int bottom, int color) {
        drawRect(left, top, right, bottom, color);
    }

    public void fillGradient(int left, int top, int right, int bottom, int startColor, int endColor) {
        drawRect(left, top, right, bottom, startColor);
    }

    public void blit(int x, int y, int u, int v, int width, int height) {
        drawTexturedModalRect(x, y, u, v, width, height);
    }
}
