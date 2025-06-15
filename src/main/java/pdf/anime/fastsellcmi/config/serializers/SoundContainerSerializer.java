package pdf.anime.fastsellcmi.config.serializers;

import org.bukkit.Sound;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import pdf.anime.fastsellcmi.utils.SoundContainer;

import java.lang.reflect.Type;

public class SoundContainerSerializer implements TypeSerializer<SoundContainer> {

    public SoundContainerSerializer() {
    }

    public SoundContainer deserialize(@NotNull Type type, @NotNull ConfigurationNode node) {
        String soundString = node.node("name").getString();
        Sound sound;
        if (soundString == null || soundString.isBlank()) {
            return null;
        }

        try {
            sound = Sound.valueOf(soundString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }

        return new SoundContainer(
                sound,
                node.node("volume").getInt(1),
                node.node("pitch").getInt(0)
        );
    }

    public void serialize(@NotNull Type type, @Nullable SoundContainer obj, @NotNull ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
        } else {
            node.node("name").set(obj.getSound().name());
            node.node("volume").set(obj.getVolume());
            node.node("pitch").set(obj.getPitch());
        }
    }
}
