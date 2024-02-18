package pdf.anime.utils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NamespacedKeysContainer {
    JavaPlugin javaPlugin;
    ArrayList<NamespacedKey> namespacedKeys;

    public NamespacedKeysContainer(JavaPlugin plugin) {
        this.javaPlugin = plugin;
        this.namespacedKeys = new ArrayList<>();

        addKey("isSell");
        addKey("isCancel");
        addKey("isBorder");
    }

    public void addKey(String key) {
        namespacedKeys.add(new NamespacedKey(javaPlugin, key));
    }

    public @NotNull NamespacedKey getKey(String key) {
        return namespacedKeys.stream().filter(key1 -> key1.getKey().equals(key)).findFirst().get();
    }
}
