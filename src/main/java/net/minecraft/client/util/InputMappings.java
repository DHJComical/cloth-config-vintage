package net.minecraft.client.util;

import org.lwjgl.input.Keyboard;

import java.util.Objects;

public final class InputMappings {
    public static final Input INPUT_INVALID = Type.KEYSYM.getOrMakeInput(-1);

    private InputMappings() {
    }

    public static Input getInputByCode(int keyCode, int scanCode) {
        return keyCode == INPUT_INVALID.getKeyCode() ? Type.SCANCODE.getOrMakeInput(scanCode) : Type.KEYSYM.getOrMakeInput(keyCode);
    }

    public static boolean isKeyDown(long window, int keyCode) {
        return keyCode >= 0 && Keyboard.isKeyDown(keyCode);
    }

    public static String getKeynameFromKeycode(int keyCode) {
        String keyName = Keyboard.getKeyName(keyCode);
        return keyName == null ? "key.keyboard.unknown" : keyName;
    }

    public static String getKeyNameFromScanCode(int scanCode) {
        return getKeynameFromKeycode(scanCode);
    }

    public enum Type {
        KEYSYM("key.keyboard"),
        SCANCODE("scancode"),
        MOUSE("key.mouse");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public Input getOrMakeInput(int keyCode) {
            return new Input(this, keyCode);
        }

        public String getName() {
            return name;
        }
    }

    public static final class Input {
        private final Type type;
        private final int keyCode;

        private Input(Type type, int keyCode) {
            this.type = type;
            this.keyCode = keyCode;
        }

        public Type getType() {
            return type;
        }

        public int getKeyCode() {
            return keyCode;
        }

        public String getTranslationKey() {
            if (type == Type.MOUSE) {
                return type.getName() + "." + keyCode;
            }
            if (keyCode < 0) {
                return "key.keyboard.unknown";
            }
            String keyName = Keyboard.getKeyName(keyCode);
            return keyName == null ? "key.keyboard.unknown" : "key.keyboard." + keyName.toLowerCase();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Input)) return false;
            Input input = (Input) o;
            return keyCode == input.keyCode && type == input.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, keyCode);
        }
    }
}
