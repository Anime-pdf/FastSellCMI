package pdf.anime.gui;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Worth.WorthItem;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import pdf.anime.config.Config;
import pdf.anime.utils.NamespacedKeysContainer;

import java.util.ArrayList;
import java.util.List;

import static com.Zrips.CMI.Modules.Economy.Economy.format;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class GuiSell implements InventoryHolder {
    final Config config;
    final NamespacedKeysContainer container;
    final Inventory inv;
    public boolean sold = false;

    public GuiSell(Config config, NamespacedKeysContainer container) {
        this.config = config;
        this.container = container;
        inv = Bukkit.createInventory(this, 9*6, config.getAsColorful("window-title"));
        initializeItems();
    }

    public Double UpdatePrice()
    {
        double Price = 0;
        for (ItemStack item : inv.getContents()) {
            if(item == null || item.getItemMeta().getPersistentDataContainer().has(container.getKey("isBorder"), PersistentDataType.INTEGER))
            {
                continue;
            }
            WorthItem worth = CMI.getInstance().getWorthManager().getWorth(item);
            if (worth != null && worth.getSellPrice() >= 1){
                Price += worth.getSellPrice() * item.getAmount();
            }
        }
        inv.setItem(9*5+4,
                createGuiItem(
                    Material.END_CRYSTAL,
                    config.getAsColorful("price-text",
                            Placeholder.unparsed("total", format(Price)
                            )
                    ), "isBorder")
        );
        return Price;
    }

    public ItemStack[] ReturnItems()
    {
        List<ItemStack> out = new ArrayList<>();
        for (ItemStack item : inv.getContents()) {
            if(item == null || item.getItemMeta().getPersistentDataContainer().has(container.getKey("isBorder"), PersistentDataType.INTEGER))
            {
                continue;
            }
            out.add(item);
        }
        return out.toArray(ItemStack[]::new);
    }
    public ItemStack[] ReturnUnsoldItems()
    {
        List<ItemStack> out = new ArrayList<>();
        for (ItemStack item : inv.getContents()) {
            if(item == null || item.getItemMeta().getPersistentDataContainer().has(container.getKey("isBorder"), PersistentDataType.INTEGER))
            {
                continue;
            }
            WorthItem worth = CMI.getInstance().getWorthManager().getWorth(item);
            if (worth == null || worth.getSellPrice() < 1){
                out.add(item);
            }
        }
        return out.toArray(ItemStack[]::new);
    }

    public void initializeItems() {
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, createGuiItem(Material.PURPLE_STAINED_GLASS_PANE, Component.text(" "), "isBorder"));
            inv.setItem(i+9*5, createGuiItem(Material.PURPLE_STAINED_GLASS_PANE, Component.text(" "), "isBorder"));
        }
        for (int i = 1; i < 5; i++) {
            inv.setItem(i*9, createGuiItem(Material.PURPLE_STAINED_GLASS_PANE, Component.text(" "), "isBorder"));
            inv.setItem((i*9)+8, createGuiItem(Material.PURPLE_STAINED_GLASS_PANE, Component.text(" "), "isBorder"));
        }
        for (int i = 0; i < 3; i++) {
            inv.setItem(9*5+i+1, createGuiItem(Material.GREEN_STAINED_GLASS_PANE, config.getAsColorful("sell-button"), "isBorder", "isSell"));
            inv.setItem(9*5+i+5, createGuiItem(Material.RED_STAINED_GLASS_PANE, config.getAsColorful("cancel-button"), "isBorder", "isCancel"));
        }
        UpdatePrice();
    }

    protected ItemStack createGuiItem(final Material material, final Component name, final String... keys) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();
        meta.displayName(name);
        for (String key : keys) {
            meta.getPersistentDataContainer().set(container.getKey(key), PersistentDataType.INTEGER, 1);
        }
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }
}
