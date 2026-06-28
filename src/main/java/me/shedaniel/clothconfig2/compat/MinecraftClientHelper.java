package me.shedaniel.clothconfig2.compat;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;

public final class MinecraftClientHelper {
    private MinecraftClientHelper() {
    }

    public static MainWindow getMainWindow() {
        return new MainWindow(Minecraft.getMinecraft());
    }
}
