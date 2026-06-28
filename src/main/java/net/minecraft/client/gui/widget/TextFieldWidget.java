package net.minecraft.client.gui.widget;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public class TextFieldWidget extends GuiTextField implements IGuiEventListener, IRenderable {
    private static final Field WIDTH_FIELD = findField("width");
    private Consumer<String> responder = text -> {
    };

    public TextFieldWidget(FontRenderer fontRenderer, int x, int y, int width, int height, String message) {
        super(0, fontRenderer, x, y, width, height);
    }

    public void setResponder(Consumer<String> responder) {
        this.responder = responder == null ? text -> {
        } : responder;
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        responder.accept(getText());
    }

    @Override
    public void writeText(String textToWrite) {
        super.writeText(textToWrite);
        responder.accept(getText());
    }

    public void tick() {
        updateCursorCounter();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        drawTextBox();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked((int) mouseX, (int) mouseY, button);
        return isFocused();
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        boolean handled = textboxKeyTyped(typedChar, keyCode);
        if (handled) {
            responder.accept(getText());
        }
        return handled;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean handled = textboxKeyTyped((char) keyCode, keyCode);
        if (handled) {
            responder.accept(getText());
        }
        return handled;
    }

    public void setWidth(int width) {
        if (WIDTH_FIELD != null) {
            try {
                WIDTH_FIELD.setInt(this, width);
            } catch (IllegalAccessException ignored) {
            }
        }
    }

    public int getWidth() {
        if (WIDTH_FIELD != null) {
            try {
                return WIDTH_FIELD.getInt(this);
            } catch (IllegalAccessException ignored) {
            }
        }
        return 0;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + getWidth() && mouseY < y + 20;
    }

    private static Field findField(String name) {
        try {
            Field field = GuiTextField.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
