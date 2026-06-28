package me.shedaniel.clothconfig2.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class DynamicElementListWidget<E extends DynamicElementListWidget.ElementEntry<E>> extends DynamicNewSmoothScrollingEntryListWidget<E> {
    
    public DynamicElementListWidget(Minecraft client, int width, int height, int top, int bottom, ResourceLocation backgroundLocation) {
        super(client, width, height, top, bottom, backgroundLocation);
    }
    
    public boolean changeFocus(boolean boolean_1) {
        boolean boolean_2 = super.changeFocus(boolean_1);
        if (boolean_2)
            this.ensureVisible(this.getFocused());
        return boolean_2;
    }
    
    protected boolean isSelected(int int_1) {
        return false;
    }
    
    @OnlyIn(Dist.CLIENT)
    public abstract static class ElementEntry<E extends ElementEntry<E>> extends Entry<E> implements INestedGuiEventHandler {
        private IGuiEventListener focused;
        private boolean dragging;
        
        public ElementEntry() {
        }
        
        public boolean isDragging() {
            return this.dragging;
        }
        
        public void setDragging(boolean boolean_1) {
            this.dragging = boolean_1;
        }
        
        public IGuiEventListener getFocused() {
            return this.focused;
        }
        
        public void setFocused(IGuiEventListener element_1) {
            this.focused = element_1;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            for (IGuiEventListener child : children()) {
                if (child.mouseClicked(mouseX, mouseY, button)) {
                    setFocused(child);
                    setDragging(true);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            boolean handled = false;
            for (IGuiEventListener child : children()) {
                handled |= child.mouseReleased(mouseX, mouseY, button);
            }
            setDragging(false);
            return handled;
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
            for (IGuiEventListener child : children()) {
                if (child.mouseScrolled(mouseX, mouseY, amount)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            return focused != null && focused.keyReleased(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char typedChar, int keyCode) {
            return focused != null && focused.charTyped(typedChar, keyCode);
        }
    }
}
