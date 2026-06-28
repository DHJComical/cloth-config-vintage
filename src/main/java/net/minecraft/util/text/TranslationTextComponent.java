package net.minecraft.util.text;

public class TranslationTextComponent extends TextComponentTranslation {
    public TranslationTextComponent(String translationKey, Object... args) {
        super(translationKey, args);
    }

    public String getString() {
        return getFormattedText();
    }
}
