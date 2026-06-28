package net.minecraft.client.gui.widget.button;

public class Button extends AbstractButton {
    private final IPressable onPress;

    public Button(int x, int y, int width, int height, String message, IPressable onPress) {
        super(x, y, width, height, message);
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        onPress.onPress(this);
    }

    public interface IPressable {
        void onPress(Button button);
    }
}
