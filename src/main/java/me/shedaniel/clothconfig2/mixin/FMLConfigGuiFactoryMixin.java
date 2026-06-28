package me.shedaniel.clothconfig2.mixin;

import me.shedaniel.clothconfig2.impl.forge.ForgeConfigScreenFactory;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.FMLConfigGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FMLConfigGuiFactory.class, remap = false)
public abstract class FMLConfigGuiFactoryMixin {
    @Inject(method = "createConfigGui", at = @At("RETURN"), cancellable = true)
    private void clothConfig$replaceFmlConfigGui(GuiScreen parentScreen, CallbackInfoReturnable<GuiScreen> cir) {
        GuiScreen screen = cir.getReturnValue();
        if (screen instanceof GuiConfig) {
            cir.setReturnValue(ForgeConfigScreenFactory.createFromGuiConfig((GuiConfig) screen));
        }
    }
}
