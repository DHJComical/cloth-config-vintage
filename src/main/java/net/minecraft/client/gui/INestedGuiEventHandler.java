package net.minecraft.client.gui;

public interface INestedGuiEventHandler extends IGuiEventListener {
    IGuiEventListener getFocused();

    void setFocused(IGuiEventListener focused);

    boolean isDragging();

    void setDragging(boolean dragging);
}
