package net.minecraft.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Screen extends GuiScreen implements IGuiEventListener, IRenderable {
    public final List<IGuiEventListener> children = new ArrayList<>();
    protected Minecraft minecraft;
    private final Object title;

    public Screen() {
        this(null);
    }

    public Screen(Object title) {
        this.title = title;
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        this.minecraft = mc;
        super.setWorldAndResolution(mc, width, height);
    }

    @Override
    public void initGui() {
        init();
    }

    protected void init() {
    }

    public void tick() {
    }

    @Override
    public void updateScreen() {
        tick();
    }

    public <T extends Widget> T addButton(T button) {
        buttonList.add(button);
        children.add(button);
        return button;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        for (IGuiEventListener child : children) {
            if (child instanceof IRenderable) {
                ((IRenderable) child).render(mouseX, mouseY, delta);
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            mouseScrolled(Mouse.getEventX(), Mouse.getEventY(), wheel > 0 ? 1 : -1);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (!mouseClicked((double) mouseX, (double) mouseY, mouseButton)) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (!mouseReleased((double) mouseX, (double) mouseY, state)) {
            super.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        boolean handled = false;
        for (IGuiEventListener child : children) {
            if (child instanceof TextFieldWidget) {
                handled |= child.charTyped(typedChar, keyCode);
            }
        }
        if (!handled && !keyPressed(keyCode, 0, 0)) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (IGuiEventListener child : children) {
            if (child.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (IGuiEventListener child : children) {
            if (child.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for (IGuiEventListener child : children) {
            if (child.mouseScrolled(mouseX, mouseY, amount)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (IGuiEventListener child : children) {
            if (child.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldCloseOnEsc() {
        return true;
    }

    public void renderDirtBackground(int tint) {
        drawDefaultBackground();
    }

    public void fillGradient(int left, int top, int right, int bottom, int startColor, int endColor) {
        drawRect(left, top, right, bottom, startColor);
    }

    public void renderTooltip(List<String> text, int x, int y) {
        drawHoveringText(text, x, y);
    }

    public void blit(int x, int y, int u, int v, int width, int height) {
        drawTexturedModalRect(x, y, u, v, width, height);
    }

    public Object getTitle() {
        return title;
    }
}
