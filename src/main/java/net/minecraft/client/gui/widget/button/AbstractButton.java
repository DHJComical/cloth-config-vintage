package net.minecraft.client.gui.widget.button;

import net.minecraft.client.gui.widget.Widget;

public abstract class AbstractButton extends Widget {
    public AbstractButton(int x, int y, int width, int height, String message) {
        super(x, y, width, height, message);
    }

    public abstract void onPress();

    @Override
    public void onClick(double mouseX, double mouseY) {
        onPress();
    }
}
