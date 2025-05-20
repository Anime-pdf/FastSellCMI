package pdf.anime.fastsellcmi.config.serializers;

import org.bukkit.Color;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class ColorSerializer implements TypeSerializer<Color> {

    public ColorSerializer() {
    }

    public Color deserialize(@NotNull Type type, ConfigurationNode node) throws SerializationException {
        if (node.isNull()) return null;

        return hexToColor(node.node("hex").get(String.class));
    }

    public void serialize(@NotNull Type type, @Nullable Color obj, @NotNull ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
        } else {
            node.node("hex").set(toHexColor(obj));
        }
    }

    public static Color hexToColor(String color) {
        try {
            if (color.charAt(0) == '#') {
                color = color.substring(1, 7);
            }

            int[] col = new int[3];

            for (int i = 0; i < 3; i++) {
                col[i] = Integer.parseInt(color.substring(i * 2, (i * 2) + 2), 16);
            }

            return Color.fromRGB(col[0], col[1], col[2]);
        } catch (Exception e) {
            return Color.BLACK;
        }
    }

    public static String toHexColor(Color col) {
        StringBuilder res = new StringBuilder();
        res.append("#");
        String val = Long.toHexString((long) col.asRGB() & 0xFFFFFF);
        res.append("0".repeat((6 - val.length())));
        res.append(val);
        return res.toString();
    }
}
