package pdf.anime.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import pdf.anime.Main;
import pdf.anime.gui.GuiSell;

import static com.Zrips.CMI.Modules.Economy.Economy.*;

public class GuiEvents implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null) return;
        if (e.getClickedInventory().getHolder() instanceof GuiSell || e.getView().getTopInventory().getHolder() instanceof GuiSell) {
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> ((GuiSell) e.getView().getTopInventory().getHolder()).UpdatePrice());

            ItemStack currItem = e.getCurrentItem();
            Player player = (Player) e.getWhoClicked();

            if(currItem == null) return;

            if (currItem.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), "isSell"), PersistentDataType.INTEGER)) {
                e.setCancelled(true);

                Double price = ((GuiSell) e.getClickedInventory().getHolder()).UpdatePrice();
                for (ItemStack item : ((GuiSell) e.getClickedInventory().getHolder()).ReturnUnsoldItems()) {
                    player.getInventory().addItem(item);
                }
                ((GuiSell) e.getClickedInventory().getHolder()).sold = true;
                player.closeInventory();
                if(price > 0)
                {
                    depositPlayer(player.getName(), price);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.config.getString("sell-msg").replace("{total}", format(price))));
                    player.playSound(player.getLocation(), Sound.valueOf(Main.config.getString("sell-sound")), 1, 1);
                }
            }
            if (currItem.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), "isCancel"), PersistentDataType.INTEGER)) {
                e.setCancelled(true);
                for (ItemStack item : ((GuiSell) e.getClickedInventory().getHolder()).ReturnItems()) {
                    player.getInventory().addItem(item);
                }
                player.closeInventory();
            }
            if (currItem.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), "isBorder"), PersistentDataType.INTEGER)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e)
    {
        if(e.getInventory().getHolder() instanceof GuiSell && !(((GuiSell) e.getInventory().getHolder()).sold))
        {
            for (ItemStack item : ((GuiSell) e.getInventory().getHolder()).ReturnItems()) {
                e.getPlayer().getInventory().addItem(item);
            }
        }
    }
}