package pdf.anime.fastsellcmi.config.serializers;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class PotionEffectSerializer implements TypeSerializer<PotionEffect> {

    public PotionEffectSerializer() {
    }

    public PotionEffect deserialize(@NotNull Type type, ConfigurationNode node) throws SerializationException {
        if (node.isNull())
            return null;

        PotionEffectType potionType = PotionEffectType.getByName(node.node("type").getString(""));
        return new PotionEffect(
                potionType != null ? potionType : PotionEffectType.UNLUCK,
                node.node("duration").getInt(),
                node.node("amplifier").getInt()
        );
    }

    public void serialize(@NotNull Type type, @Nullable PotionEffect obj, @NotNull ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
        } else {
            node.node("type").set(obj.getType().getName());
            node.node("duration").set(obj.getDuration());
            node.node("amplifier").set(obj.getAmplifier());
        }
    }
}
