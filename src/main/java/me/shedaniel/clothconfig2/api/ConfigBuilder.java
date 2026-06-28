package me.shedaniel.clothconfig2.api;

import me.shedaniel.clothconfig2.impl.ConfigBuilderImpl;
import me.shedaniel.clothconfig2.impl.ConfigEntryBuilderImpl;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public interface ConfigBuilder {
    
    @SuppressWarnings("deprecation")
    static ConfigBuilder create() {
        return new ConfigBuilderImpl();
    }
    
    /**
     * @deprecated Use {@link ConfigBuilder#create()}
     */
    @Deprecated
    static ConfigBuilder create(GuiScreen parent, String title) {
        return create().setParentScreen(parent).setTitle(title);
    }
    
    ConfigBuilder setFallbackCategory(ConfigCategory fallbackCategory);
    
    GuiScreen getParentScreen();
    
    ConfigBuilder setParentScreen(GuiScreen parent);
    
    String getTitle();
    
    ConfigBuilder setTitle(String title);

    default ConfigBuilder setTitle(CharSequence title) {
        return setTitle(title.toString());
    }
    
    boolean isEditable();
    
    ConfigBuilder setEditable(boolean editable);
    
    ConfigCategory getOrCreateCategory(String categoryKey);

    default ConfigCategory getOrCreateCategory(CharSequence categoryKey) {
        return getOrCreateCategory(categoryKey.toString());
    }
    
    ConfigBuilder removeCategory(String categoryKey);

    default ConfigBuilder removeCategory(CharSequence categoryKey) {
        return removeCategory(categoryKey.toString());
    }
    
    ConfigBuilder removeCategoryIfExists(String categoryKey);

    default ConfigBuilder removeCategoryIfExists(CharSequence categoryKey) {
        return removeCategoryIfExists(categoryKey.toString());
    }
    
    boolean hasCategory(String category);

    default boolean hasCategory(CharSequence category) {
        return hasCategory(category.toString());
    }
    
    ConfigBuilder setShouldTabsSmoothScroll(boolean shouldTabsSmoothScroll);
    
    boolean isTabsSmoothScrolling();
    
    ConfigBuilder setShouldListSmoothScroll(boolean shouldListSmoothScroll);
    
    boolean isListSmoothScrolling();
    
    ConfigBuilder setDoesConfirmSave(boolean confirmSave);
    
    boolean doesConfirmSave();
    
    ConfigBuilder setDoesProcessErrors(boolean processErrors);
    
    boolean doesProcessErrors();
    
    ResourceLocation getDefaultBackgroundTexture();
    
    ConfigBuilder setDefaultBackgroundTexture(ResourceLocation texture);
    
    Runnable getSavingRunnable();
    
    ConfigBuilder setSavingRunnable(Runnable runnable);
    
    Consumer<GuiScreen> getAfterInitConsumer();
    
    ConfigBuilder setAfterInitConsumer(Consumer<GuiScreen> afterInitConsumer);
    
    default ConfigBuilder alwaysShowTabs() {
        return setAlwaysShowTabs(true);
    }
    
    boolean isAlwaysShowTabs();
    
    ConfigBuilder setAlwaysShowTabs(boolean alwaysShowTabs);
    
    ConfigBuilder setTransparentBackground(boolean transparentBackground);
    
    default ConfigBuilder transparentBackground() {
        return setTransparentBackground(true);
    }
    
    default ConfigBuilder solidBackground() {
        return setTransparentBackground(false);
    }
    
    default ConfigEntryBuilder getEntryBuilder() {
        return entryBuilder();
    }
    
    default ConfigEntryBuilder entryBuilder() {
        return ConfigEntryBuilderImpl.create();
    }
    
    Screen build();
    
}
