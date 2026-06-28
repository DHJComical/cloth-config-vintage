package net.minecraft.client.resources.language;

public final class I18n {
    private I18n() {
    }

    public static String get(String key) {
        return net.minecraft.client.resources.I18n.format(key);
    }
}
