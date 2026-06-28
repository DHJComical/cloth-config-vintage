package me.shedaniel.clothconfig2.api;

import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.math.Rectangle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractConfigListEntry<T> extends AbstractConfigEntry<T> {
    private String fieldName;
    private boolean editable = true;
    private boolean requiresRestart;
    
    public AbstractConfigListEntry(String fieldName, boolean requiresRestart) {
        this.fieldName = fieldName;
        this.requiresRestart = requiresRestart;
    }
    
    @Override
    public boolean isRequiresRestart() {
        return requiresRestart;
    }
    
    @Override
    public void setRequiresRestart(boolean requiresRestart) {
        this.requiresRestart = requiresRestart;
    }
    
    public boolean isEditable() {
        return getScreen().isEditable() && editable;
    }
    
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    public final int getPreferredTextColor() {
        return getConfigError().isPresent() ? 16733525 : 16777215;
    }

    public Rectangle getEntryArea(int x, int y, int entryWidth, int entryHeight) {
        return new Rectangle(getParent().left, y, getParent().right - getParent().left, getItemHeight() - 4);
    }

    public boolean isMouseInside(int mouseX, int mouseY, int x, int y, int entryWidth, int entryHeight) {
        return getParent().isMouseOver(mouseX, mouseY) && getEntryArea(x, y, entryWidth, entryHeight).contains(mouseX, mouseY);
    }

    @Override
    public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        if (isMouseInside(mouseX, mouseY, x, y, entryWidth, entryHeight) && getParent() instanceof ClothConfigScreen.ListWidget) {
            ((ClothConfigScreen.ListWidget<AbstractConfigEntry<T>>) getParent()).thisTimeTarget = getEntryArea(x, y, entryWidth, entryHeight);
        }
    }
    
    @Override
    public String getFieldName() {
        return fieldName;
    }
}
