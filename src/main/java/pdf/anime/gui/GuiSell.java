package pdf.anime.gui;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Worth.WorthItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pdf.anime.Main;

import java.util.ArrayList;
import java.util.List;

import static com.Zrips.CMI.Modules.Economy.Economy.format;

public class GuiSell implements InventoryHolder {
    private final Inventory inv;
    public GuiSell() {
        inv = Bukkit.createInventory(this, 9*6, Main.GetColorStringConfig("window-title"));
        initializeItems();
    }

    public Double UpdatePrice()
    {
        double Price = 0;
        for (ItemStack item : inv.getContents()) {
            if(item == null || item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), "isBorder"), PersistentDataType.INTEGER))
            {
                continue;
            }
            WorthItem worth = CMI.getInstance().getWorthManager().getWorth(item);
            if (worth != null && worth.getSellPrice() >= 1){
                Price += worth.getSellPrice() * item.getAmount();
            }
        }
        inv.setItem(9*5+4, createGuiItem(Material.END_CRYSTAL, Main.GetColorStringConfig("price-text").replace("{total}", format(Price)), "isBorder"));
        return Price;
    }

    public ItemStack[] ReturnItems()
    {
        List<ItemStack> out = new ArrayList<>();
        for (ItemStack item : inv.getContents()) {
            if(item == null || item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), "isBorder"), PersistentDataType.INTEGER))
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
            if(item == null || item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), "isBorder"), PersistentDataType.INTEGER))
            {
                continue;
            }
            WorthItem worth = CMI.getInstance().getWorthManager().getWorth(item);
            if (worth != null || worth.getSellPrice() < 1){
                out.add(item);
            }
        }
        return out.toArray(ItemStack[]::new);
    }

    public void initializeItems() {
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, createGuiItem(Material.PURPLE_STAINED_GLASS_PANE, " ", "isBorder"));
            inv.setItem(i+9*5, createGuiItem(Material.PURPLE_STAINED_GLASS_PANE, " ", "isBorder"));
        }
        for (int i = 1; i < 5; i++) {
            inv.setItem(i*9, createGuiItem(Material.PURPLE_STAINED_GLASS_PANE, " ", "isBorder"));
            inv.setItem((i*9)+8, createGuiItem(Material.PURPLE_STAINED_GLASS_PANE, " ", "isBorder"));
        }
        for (int i = 0; i < 3; i++) {
            inv.setItem(9*5+i+1, createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.translateAlternateColorCodes('&', Main.GetColorStringConfig("sell-button")), "isBorder", "isSell"));
            inv.setItem(9*5+i+5, createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.translateAlternateColorCodes('&', Main.GetColorStringConfig("cancel-button")), "isBorder", "isCancel"));
        }
        UpdatePrice();
    }

    protected ItemStack createGuiItem(final Material material, final String name, final String... keys) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        for (String key : keys) {
            meta.getPersistentDataContainer().set(new NamespacedKey(Main.getInstance(), key), PersistentDataType.INTEGER, 1);
        }
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
