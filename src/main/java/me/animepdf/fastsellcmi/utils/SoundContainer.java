package me.animepdf.fastsellcmi.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Sound;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SoundContainer {
    final Sound sound;
    final int volume;
    final int pitch;

    public SoundContainer(Sound sound, int volume, int pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public SoundContainer(Sound sound) {
        this(sound, 1, 0);
    }
}
