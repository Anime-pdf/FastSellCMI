package me.animepdf.fastsellcmi.listeners;

import com.Zrips.CMI.CMI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import me.animepdf.fastsellcmi.config.ConfigContainer;
import me.animepdf.fastsellcmi.config.SellMenuConfig;
import me.animepdf.fastsellcmi.menus.FastSellMenu;
import me.animepdf.fastsellcmi.services.PDCService;
import me.animepdf.fastsellcmi.utils.BukkitRunner;
import me.animepdf.fastsellcmi.utils.PlaceholderUtils;
import me.animepdf.fastsellcmi.utils.SoundContainer;

import java.util.List;
import java.util.Map;

import static com.Zrips.CMI.Modules.Economy.Economy.format;

public class FastSellMenuListener implements Listener {
    private final ConfigContainer configContainer;
    private final PDCService pdcService;
    private final BukkitRunner runnerService;

    public FastSellMenuListener(ConfigContainer configContainer, PDCService pdcService, BukkitRunner runnerService) {
        this.configContainer = configContainer;
        this.pdcService = pdcService;
        this.runnerService = runnerService;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof FastSellMenu menu)) {
            return;
        }
        runnerService.runLater(() -> menu.updateTotalPrice(true), 1);

        ItemStack currItem = event.getCurrentItem();
        String buttonType = pdcService.getButtonType(currItem);

        if (buttonType == null || buttonType.isEmpty()) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        switch (buttonType) {
            case SellMenuConfig.SELL_BUTTON_TYPE:
                handleSell(player, menu);
                break;
            case SellMenuConfig.CANCEL_BUTTON_TYPE:
                handleCancel(player, menu);
                break;
        }
    }

    private void handleSell(Player player, FastSellMenu menu) {
        menu.setTransactionInProgress(true);

        double price = menu.getTotalPrice();
        List<ItemStack> unsellableItems = menu.getUnsellableItems();
        List<ItemStack> sellableItems = menu.getSellableItems();

        menu.getInventory().removeItem(unsellableItems.toArray(new ItemStack[0]));
        menu.getInventory().removeItem(sellableItems.toArray(new ItemStack[0]));

        if (!unsellableItems.isEmpty()) {
            Map<Integer, ItemStack> didNotFit = player.getInventory().addItem(unsellableItems.toArray(new ItemStack[0]));

            if (!didNotFit.isEmpty()) {
                SoundContainer inventoryFullSound = configContainer.getLanguageConfig().inventoryFullSound;
                player.sendMessage(configContainer.getLanguageConfig().inventoryFullMessage);
                player.playSound(player.getLocation(), inventoryFullSound.getSound(), inventoryFullSound.getVolume(), inventoryFullSound.getPitch());
                for (ItemStack itemToDrop : didNotFit.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), itemToDrop);
                }
            }
        }

        if (price > 0) {
            CMI.getInstance().getPlayerManager().getUser(player).deposit(price);
        }

        menu.setSold(true);
        player.closeInventory();

        if (price > 0) {
            CMI.getInstance().getPlayerManager().getUser(player).deposit(price);

            Component sellMessage = PlaceholderUtils.replacePlaceholder(
                    configContainer.getLanguageConfig().sellMessage,
                    "{total}",
                    format(price)
            );
            SoundContainer sellSound = configContainer.getLanguageConfig().sellSound;

            player.sendMessage(sellMessage);
            player.playSound(player.getLocation(), sellSound.getSound(), sellSound.getVolume(), sellSound.getPitch());
        } else {
            SoundContainer noItemsSoldSound = configContainer.getLanguageConfig().noItemsSoldSound;

            player.sendMessage(configContainer.getLanguageConfig().noItemsSoldMessage);
            player.playSound(player.getLocation(), noItemsSoldSound.getSound(), noItemsSoldSound.getVolume(), noItemsSoldSound.getPitch());
        }
    }

    private void handleCancel(Player player, FastSellMenu menu) {
        menu.setSold(true);

        returnItemsOnClose(player, menu);
        player.closeInventory();

        SoundContainer cancelSound = configContainer.getLanguageConfig().getCancelSound();
        player.sendMessage(configContainer.getLanguageConfig().getCancelMessage());
        player.playSound(player.getLocation(), cancelSound.getSound(), cancelSound.getVolume(), cancelSound.getPitch());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof FastSellMenu menu)) {
            return;
        }

        if (menu.isSold() || menu.isTransactionInProgress()) {
            return;
        }

        Player player = (Player) event.getPlayer();
        returnItemsOnClose(player, menu);

        SoundContainer cancelSound = configContainer.getLanguageConfig().getCancelSound();
        player.sendMessage(configContainer.getLanguageConfig().getCancelMessage());
        player.playSound(player.getLocation(), cancelSound.getSound(), cancelSound.getVolume(), cancelSound.getPitch());
    }

    private void returnItemsOnClose(Player player, FastSellMenu menu) {
        List<ItemStack> itemsToReturn = menu.getItems();
        if (itemsToReturn.isEmpty()) {
            return;
        }

        Map<Integer, ItemStack> didNotFit = player.getInventory().addItem(itemsToReturn.toArray(new ItemStack[0]));

        if (!didNotFit.isEmpty()) {
            player.sendMessage(configContainer.getLanguageConfig().inventoryFullMessage);
            for (ItemStack itemToDrop : didNotFit.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), itemToDrop);
            }
        }
    }
}
