package net.minecraft.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;

public abstract class Widget extends GuiButton implements IGuiEventListener, IRenderable {
    public boolean active = true;
    public float alpha = 1.0F;

    public Widget(int x, int y, int width, int height, String message) {
        super(0, x, y, width, height, message);
    }

    public void setMessage(String message) {
        this.displayString = message;
    }

    public String getMessage() {
        return displayString;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return this.width;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.enabled = this.active;
        drawButton(Minecraft.getMinecraft(), mouseX, mouseY, delta);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            this.hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
            renderButton(mouseX, mouseY, partialTicks);
        }
    }

    public void renderButton(int mouseX, int mouseY, float delta) {
        super.drawButton(Minecraft.getMinecraft(), mouseX, mouseY, delta);
    }

    public void onClick(double mouseX, double mouseY) {
    }

    public void onRelease(double mouseX, double mouseY) {
    }

    protected boolean clicked(double mouseX, double mouseY) {
        return visible && active && isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && clicked(mouseX, mouseY)) {
            playPressSound(Minecraft.getMinecraft().getSoundHandler());
            onClick(mouseX, mouseY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        onRelease(mouseX, mouseY);
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    public boolean isHovered() {
        return hovered;
    }

    public int getYImage(boolean hovered) {
        if (!active) {
            return 0;
        }
        return hovered ? 2 : 1;
    }

    public void blit(int x, int y, int u, int v, int width, int height) {
        drawTexturedModalRect(x, y, u, v, width, height);
    }

    public void fillGradient(int left, int top, int right, int bottom, int startColor, int endColor) {
        drawRect(left, top, right, bottom, startColor);
    }
}
