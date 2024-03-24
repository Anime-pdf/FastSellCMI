package pdf.anime.fastsellcmi.config.serializers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ComponentSerializer implements TypeSerializer<Component> {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public ComponentSerializer() {
    }

    public Component deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.isNull()) {
            return null;
        } else if (node.isList()) {
            AtomicReference<TextComponent> atomicComponent = new AtomicReference(Component.empty());
            List<String> list = node.getList(String.class);
            list.forEach(withCounterConsumer((id, string) -> {
                atomicComponent.getAndUpdate((component) -> {
                    component = (TextComponent)component.append(this.deserialize(string));
                    if (id + 1 != list.size()) {
                        component = (TextComponent)component.append(Component.newline());
                    }

                    return component;
                });
            }));
            return (Component)atomicComponent.get();
        } else {
            return this.deserialize(node.getString());
        }
    }

    public void serialize(Type type, @Nullable Component obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw((Object)null);
        } else {
            String message = this.serialize(obj);
            if (message.contains("<br>")) {
                String[] striped = message.split("<br>");
                node.setList(String.class, Arrays.stream(striped).filter((string) -> {
                    return !string.isEmpty();
                }).toList());
            } else {
                node.set(message);
            }
        }

    }

    private Component deserialize(String text) {
        return MINI_MESSAGE.deserialize(text);
    }

    private String serialize(Component component) {
        return (String)MINI_MESSAGE.serialize(component);
    }

    private static <T> Consumer<T> withCounterConsumer(BiConsumer<Integer, T> predicate) {
        AtomicInteger counter = new AtomicInteger();
        return (obj) -> {
            predicate.accept(counter.getAndIncrement(), obj);
        };
    }
}
