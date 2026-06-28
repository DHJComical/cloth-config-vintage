package me.shedaniel.clothconfig2.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClothRequiresRestartScreen extends ConfirmScreen {
    
    public ClothRequiresRestartScreen(GuiScreen parent) {
        super(t -> {
            if (t)
                Minecraft.getMinecraft().shutdown();
            else
                Minecraft.getMinecraft().displayGuiScreen(parent);
        }, new TranslationTextComponent("text.cloth-config.restart_required"), new TranslationTextComponent("text.cloth-config.restart_required_sub"), I18n.format("text.cloth-config.exit_minecraft"), I18n.format("text.cloth-config.ignore_restart"));
    }
    
}
