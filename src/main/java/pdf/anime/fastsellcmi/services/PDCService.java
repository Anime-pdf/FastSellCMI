package pdf.anime.fastsellcmi.services;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PDCService {
    private final NamespacedKey buttonTypeKey;

    public PDCService(JavaPlugin plugin) {
        this.buttonTypeKey = new NamespacedKey(plugin, "button-type");
    }

    public ItemStack applyButtonType(ItemStack item, String buttonType) {
        if (buttonType == null || buttonType.isEmpty()) {
            return item;
        }

        ItemStack clone = item.clone();
        ItemMeta meta = clone.getItemMeta();
        if (meta == null) {
            return clone;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(getButtonTypeKey(), PersistentDataType.STRING, buttonType);
        clone.setItemMeta(meta);

        return clone;
    }

    public String getButtonType(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.get(getButtonTypeKey(), PersistentDataType.STRING);
    }
}
