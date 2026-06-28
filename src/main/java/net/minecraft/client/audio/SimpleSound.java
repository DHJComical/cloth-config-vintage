package net.minecraft.client.audio;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class SimpleSound extends PositionedSoundRecord {
    public SimpleSound(SoundEvent sound, float pitch) {
        super(sound, SoundCategory.MASTER, 1.0F, pitch, 0.0F, 0.0F, 0.0F);
    }

    public static SimpleSound master(SoundEvent sound, float pitch) {
        return new SimpleSound(sound, pitch);
    }
}
