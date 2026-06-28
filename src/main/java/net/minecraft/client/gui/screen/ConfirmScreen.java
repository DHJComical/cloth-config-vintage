package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.widget.button.Button;

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
        addButton(new Button(width / 2 - 105, height / 6 + 96, 100, 20, yesText, button -> callback.accept(true)));
        addButton(new Button(width / 2 + 5, height / 6 + 96, 100, 20, noText, button -> callback.accept(false)));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, String.valueOf(title), width / 2, height / 6 + 40, 0xffffff);
        drawCenteredString(fontRenderer, String.valueOf(message), width / 2, height / 6 + 60, 0xffffff);
        super.render(mouseX, mouseY, delta);
    }
}
