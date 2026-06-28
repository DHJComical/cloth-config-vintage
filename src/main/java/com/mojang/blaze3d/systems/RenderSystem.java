package com.mojang.blaze3d.systems;

import org.lwjgl.opengl.GL11;

public final class RenderSystem {
    private RenderSystem() {
    }

    public static void color4f(float red, float green, float blue, float alpha) {
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void enableBlend() {
        GL11.glEnable(GL11.GL_BLEND);
    }

    public static void disableBlend() {
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void blendFuncSeparate(int srcRgb, int dstRgb, int srcAlpha, int dstAlpha) {
        GL11.glBlendFunc(srcRgb, dstRgb);
    }

    public static void blendFunc(int src, int dst) {
        GL11.glBlendFunc(src, dst);
    }

    public static void disableAlphaTest() {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

    public static void enableAlphaTest() {
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }

    public static void shadeModel(int mode) {
        GL11.glShadeModel(mode);
    }

    public static void disableTexture() {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public static void enableTexture() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void disableLighting() {
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    public static void disableFog() {
        GL11.glDisable(GL11.GL_FOG);
    }

    public static void disableDepthTest() {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    public static void pushMatrix() {
        GL11.glPushMatrix();
    }

    public static void popMatrix() {
        GL11.glPopMatrix();
    }

    public static void translatef(float x, float y, float z) {
        GL11.glTranslatef(x, y, z);
    }
}
