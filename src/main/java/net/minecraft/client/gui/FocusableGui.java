package net.minecraft.client.gui;

public class FocusableGui implements INestedGuiEventHandler {
    private IGuiEventListener focused;
    private boolean dragging;

    @Override
    public IGuiEventListener getFocused() {
        return focused;
    }

    @Override
    public void setFocused(IGuiEventListener focused) {
        this.focused = focused;
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public boolean changeFocus(boolean focus) {
        return focus;
    }

    protected void fill(int left, int top, int right, int bottom, int color) {
        Gui.drawRect(left, top, right, bottom, color);
    }

    protected void fillGradient(int left, int top, int right, int bottom, int startColor, int endColor) {
        Gui.drawRect(left, top, right, bottom, startColor);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return focused != null && focused.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return focused != null && focused.keyPressed(keyCode, scanCode, modifiers);
    }
}
