package me.shedaniel.clothconfig2.mixin;

import me.shedaniel.clothconfig2.impl.forge.ForgeConfigScreenFactory;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.DefaultGuiFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DefaultGuiFactory.class, remap = false)
public abstract class DefaultGuiFactoryMixin {
    @Shadow
    protected String modid;

    @Shadow
    protected String title;

    @Inject(method = "createConfigGui", at = @At("HEAD"), cancellable = true)
    private void clothConfig$replaceDefaultConfigGui(GuiScreen parentScreen, CallbackInfoReturnable<GuiScreen> cir) {
        cir.setReturnValue(ForgeConfigScreenFactory.createDefaultConfigScreen(parentScreen, modid, title));
    }
}
