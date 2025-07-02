package me.animepdf.fastsellcmi.config.serializers;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FireworkEffectSerializer implements TypeSerializer<FireworkEffect> {

    public FireworkEffectSerializer() {
    }

    public FireworkEffect deserialize(@NotNull Type type, ConfigurationNode node) throws SerializationException {
        if (node.isNull())
            return null;

        return FireworkEffect.builder()
                .flicker(node.node("flicker").getBoolean(false))
                .trail(node.node("trail").getBoolean(false))
                .with(node.node("type").get(FireworkEffect.Type.class, FireworkEffect.Type.BALL))
                .withColor(node.node("colors").getList(Color.class, new ArrayList<>()))
                .withFade(node.node("fade").getList(Color.class, new ArrayList<>()))
                .build();
    }

    public void serialize(@NotNull Type type, @Nullable FireworkEffect obj, @NotNull ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
        } else {
            if (obj.hasFlicker()) {
                node.node("flicker").set(true);
            }

            if (obj.hasTrail()) {
                node.node("trail").set(true);
            }

            node.node("type").set(obj.getType());
            if (!obj.getColors().isEmpty()) {
                node.node("colors").setList(Color.class, obj.getColors());
            }

            if (!obj.getFadeColors().isEmpty()) {
                node.node("fade").setList(Color.class, obj.getFadeColors());
            }
        }
    }
}
