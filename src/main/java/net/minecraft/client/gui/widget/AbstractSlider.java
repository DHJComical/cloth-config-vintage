package net.minecraft.client.gui.widget;

public abstract class AbstractSlider extends Widget {
    protected double value;

    protected AbstractSlider(int x, int y, int width, int height, double value) {
        super(x, y, width, height, "");
        this.value = value;
    }

    public abstract void updateMessage();

    protected abstract void applyValue();

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            updateValue(mouseX);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (active && visible && button == 0 && isMouseOver(mouseX, mouseY)) {
            updateValue(mouseX);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 203 || keyCode == 263) {
            value = Math.max(0.0D, value - 0.01D);
            applyValue();
            updateMessage();
            return true;
        }
        if (keyCode == 205 || keyCode == 262) {
            value = Math.min(1.0D, value + 0.01D);
            applyValue();
            updateMessage();
            return true;
        }
        return false;
    }

    private void updateValue(double mouseX) {
        value = (mouseX - (x + 4)) / (double) (width - 8);
        value = Math.max(0.0D, Math.min(1.0D, value));
        applyValue();
        updateMessage();
    }
}
