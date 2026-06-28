package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class ConfirmScreen extends Screen {
    private final BooleanConsumer callback;
    private final Object title;
    private final Object message;
    private final String yesText;
    private final String noText;

    public ConfirmScreen(BooleanConsumer callback, Object title, Object message, String yesText, String noText) {
        this.callback = callback;
        this.title = title;
        this.message = message;
        this.yesText = yesText;
        this.noText = noText;
    }

    @Override
    protected void init() {
        int minButtonWidth = 150;
        int maxButtonWidth = Math.max(minButtonWidth, (width - 20) / 2);
        int leftButtonWidth = Math.min(maxButtonWidth, Math.max(minButtonWidth, fontRenderer.getStringWidth(yesText) + 20));
        int rightButtonWidth = Math.min(maxButtonWidth, Math.max(minButtonWidth, fontRenderer.getStringWidth(noText) + 20));
        int gap = 10;
        int totalWidth = leftButtonWidth + rightButtonWidth + gap;
        int startX = (width - totalWidth) / 2;
        int buttonY = height / 6 + 96;
        addButton(new Button(startX, buttonY, leftButtonWidth, 20, yesText, button -> callback.accept(true)));
        addButton(new Button(startX + leftButtonWidth + gap, buttonY, rightButtonWidth, 20, noText, button -> callback.accept(false)));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, stringify(title), width / 2, 70, 0xffffff);
        List<String> lines = fontRenderer.listFormattedStringToWidth(stringify(message), width - 50);
        int lineY = 90;
        for (String line : lines) {
            drawCenteredString(fontRenderer, line, width / 2, lineY, 0xffffff);
            lineY += fontRenderer.FONT_HEIGHT;
        }
        super.render(mouseX, mouseY, delta);
    }

    private static String stringify(Object text) {
        if (text instanceof TranslationTextComponent) {
            return ((TranslationTextComponent) text).getString();
        }
        return String.valueOf(text);
    }
}
