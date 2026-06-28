package net.minecraft.network.chat;

import net.minecraft.client.resources.I18n;

public class Component implements CharSequence {
    private final String key;
    private final boolean translate;

    private Component(String key, boolean translate) {
        this.key = key;
        this.translate = translate;
    }

    public static Component translatable(String key) {
        return new Component(key, true);
    }

    public static Component literal(String text) {
        return new Component(text, false);
    }

    public String getString() {
        return translate ? I18n.format(key) : key;
    }

    @Override
    public int length() {
        return getString().length();
    }

    @Override
    public char charAt(int index) {
        return getString().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return getString().subSequence(start, end);
    }

    @Override
    public String toString() {
        return getString();
    }
}
