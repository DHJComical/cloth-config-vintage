package me.shedaniel.math.impl;

import me.shedaniel.clothconfig2.compat.MinecraftClientHelper;
import me.shedaniel.math.Point;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.input.Mouse;

@OnlyIn(Dist.CLIENT)
public class PointHelper {
    public static Point ofMouse() {
        Minecraft client = Minecraft.getMinecraft();
        double mx = Mouse.getX() * (double) MinecraftClientHelper.getMainWindow().getScaledWidth() / (double) MinecraftClientHelper.getMainWindow().getWidth();
        double my = MinecraftClientHelper.getMainWindow().getScaledHeight() - Mouse.getY() * (double) MinecraftClientHelper.getMainWindow().getScaledHeight() / (double) MinecraftClientHelper.getMainWindow().getHeight() - 1;
        return new Point(mx, my);
    }
    
    public static int getMouseX() {
        return ofMouse().x;
    }
    
    public static int getMouseY() {
        return ofMouse().y;
    }
}
