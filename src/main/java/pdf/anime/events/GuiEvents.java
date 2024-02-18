package pdf.anime.events;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import pdf.anime.config.Config;
import pdf.anime.gui.GuiSell;
import pdf.anime.utils.BukkitRunner;
import pdf.anime.utils.NamespacedKeysContainer;

import static com.Zrips.CMI.Modules.Economy.Economy.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GuiEvents implements Listener {
    Config config;
    NamespacedKeysContainer container;
    BukkitRunner runner;

    public GuiEvents(Config config, NamespacedKeysContainer container, BukkitRunner runner) {
        this.config = config;
        this.container = container;
        this.runner = runner;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null)
            return;

        if (!(e.getClickedInventory().getHolder() instanceof GuiSell) && (e.getView().getTopInventory().getHolder() instanceof GuiSell)) {
            return;
        }

        GuiSell inventory = (GuiSell) e.getView().getTopInventory().getHolder();
        if(inventory == null)
            return;

        runner.run(inventory::UpdatePrice);

        ItemStack currItem = e.getCurrentItem();
        Player player = (Player) e.getWhoClicked();

        if(currItem == null) return;

        if(!currItem.getItemMeta().getPersistentDataContainer().has(container.getKey("isSell"), PersistentDataType.INTEGER))
            return;

        e.setCancelled(true);

        Double price = inventory.UpdatePrice();
        for (ItemStack item : inventory.ReturnUnsoldItems()) {
            player.getInventory().addItem(item);
        }

        inventory.sold = true;
        player.closeInventory();

        if(price > 0)
        {
            depositPlayer(player.getName(), price);
            player.sendMessage(
                    config.getAsColorful(
                            "sell-msg",
                            Placeholder.unparsed("total", format(price)
                            )
                    )
            );

            player.playSound(player.getLocation(), Sound.valueOf(config.get("sell-sound")), 1, 1);
        }

        if (currItem.getItemMeta().getPersistentDataContainer().has(container.getKey("isCancel"), PersistentDataType.INTEGER)) {
            e.setCancelled(true);
            for (ItemStack item : inventory.ReturnItems()) {
                player.getInventory().addItem(item);
            }
            player.closeInventory();
        }
        if (currItem.getItemMeta().getPersistentDataContainer().has(container.getKey("isBorder"), PersistentDataType.INTEGER)) {
            e.setCancelled(true);
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