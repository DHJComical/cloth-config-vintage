package net.minecraft.client;

import net.minecraft.client.gui.ScaledResolution;

public class MainWindow {
    private final Minecraft minecraft;

    public MainWindow(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public int getWidth() {
        return minecraft.displayWidth;
    }

    public int getHeight() {
        return minecraft.displayHeight;
    }

    public int getScaledWidth() {
        return new ScaledResolution(minecraft).getScaledWidth();
    }

    public int getScaledHeight() {
        return new ScaledResolution(minecraft).getScaledHeight();
    }

    public double getGuiScaleFactor() {
        ScaledResolution scaled = new ScaledResolution(minecraft);
        return (double) minecraft.displayWidth / (double) scaled.getScaledWidth();
    }

    public long getHandle() {
        return 0L;
    }
}
