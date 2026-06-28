package net.minecraft.resources;

import net.minecraft.util.ResourceLocation;

public class Identifier extends ResourceLocation {
    public Identifier(String location) {
        super(location);
    }

    public Identifier(String namespace, String path) {
        super(namespace, path);
    }

    public static Identifier parse(String location) {
        return new Identifier(location);
    }

    public static Identifier tryParse(String location) {
        try {
            return new Identifier(location);
        } catch (RuntimeException e) {
            return null;
        }
    }
}
