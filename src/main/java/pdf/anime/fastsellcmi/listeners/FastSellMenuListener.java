package pdf.anime.fastsellcmi.listeners;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import pdf.anime.fastsellcmi.config.ConfigContainer;
import pdf.anime.fastsellcmi.config.SellMenuConfig;
import pdf.anime.fastsellcmi.menus.FastSellMenu;
import pdf.anime.fastsellcmi.utils.BukkitRunner;

import static com.Zrips.CMI.Modules.Economy.Economy.depositPlayer;
import static com.Zrips.CMI.Modules.Economy.Economy.format;

public class FastSellMenuListener implements Listener {
    ConfigContainer configContainer;
    BukkitRunner runner;

    public FastSellMenuListener(ConfigContainer configContainer, BukkitRunner runner) {
        this.configContainer = configContainer;
        this.runner = runner;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null) return;
        if (e.getView().getTopInventory().getHolder() instanceof FastSellMenu menu) {
            runner.run(() -> menu.updateTotalPrice(true));

            ItemStack currItem = e.getCurrentItem();
            Player player = (Player) e.getWhoClicked();

            if(currItem == null) return;

            NBTItem nbtItem = new NBTItem(currItem);

            if (!nbtItem.hasTag("button-type")) {
                return;
            }

            e.setCancelled(true);

            String type = nbtItem.getString("button-type");

            if (type.equals(SellMenuConfig.SELL_BUTTON_TYPE)) {
                e.setCancelled(true);

                double price = menu.getTotalPrice();
                for (ItemStack item : menu.getUnsellableItems()) {
                    player.getInventory().addItem(item);
                }
                menu.setSold(true);
                player.closeInventory();
                if(price > 0)
                {
                    depositPlayer(player.getName(), price);
                    player.sendMessage(configContainer.getLanguageConfig().sellMessage.replaceText(TextReplacementConfig.builder().match("<total>").replacement(Component.text(format(price))).build()));
                    player.playSound(player.getLocation(), Sound.valueOf(configContainer.getLanguageConfig().sellSound), 1, 1);
                }
            }
            if (type.equals(SellMenuConfig.CANCEL_BUTTON_TYPE)) {
                e.setCancelled(true);
                for (ItemStack item : menu.getUnsellableItems()) {
                    player.getInventory().addItem(item);
                }
                player.closeInventory();
                player.sendMessage(configContainer.getLanguageConfig().cancelMessage);
                player.playSound(player.getLocation(), Sound.valueOf(configContainer.getLanguageConfig().cancelSound), 1, 1);
            }
            if (type.equals(SellMenuConfig.WALL_BUTTON_TYPE) || type.equals(SellMenuConfig.PRICE_BUTTON_TYPE)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e)
    {
        if(e.getInventory().getHolder() instanceof FastSellMenu menu && !menu.isSold())
        {
            for (ItemStack item : menu.getUnsellableItems()) {
                e.getPlayer().getInventory().addItem(item);
            }
        }
    }
}
