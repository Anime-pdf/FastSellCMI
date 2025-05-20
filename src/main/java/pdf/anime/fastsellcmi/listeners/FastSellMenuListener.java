package pdf.anime.fastsellcmi.listeners;

import com.Zrips.CMI.CMI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import pdf.anime.fastsellcmi.config.ConfigContainer;
import pdf.anime.fastsellcmi.config.SellMenuConfig;
import pdf.anime.fastsellcmi.menus.FastSellMenu;
import pdf.anime.fastsellcmi.services.PDCService;
import pdf.anime.fastsellcmi.utils.BukkitRunner;
import pdf.anime.fastsellcmi.utils.PlaceholderUtils;
import pdf.anime.fastsellcmi.utils.SoundContainer;

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
            case SellMenuConfig.WALL_BUTTON_TYPE:
            case SellMenuConfig.PRICE_BUTTON_TYPE:
                break;
            case SellMenuConfig.SELL_BUTTON_TYPE:
                handleSell(player, menu);
                break;
            case SellMenuConfig.CANCEL_BUTTON_TYPE:
                handleCancel(player, menu);
                break;
        }
    }

    private void handleSell(Player player, FastSellMenu menu) {
        double price = menu.getTotalPrice();

        menu.getUnsellableItems().forEach(item -> player.getInventory().addItem(item));
        menu.getItems().forEach(item -> menu.getInventory().removeItem(item));

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
        player.closeInventory();
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof FastSellMenu menu && !menu.isSold()) {
            Player player = (Player)event.getPlayer();
            SoundContainer cancelSound = configContainer.getLanguageConfig().cancelSound;
            player.sendMessage(configContainer.getLanguageConfig().cancelMessage);
            player.playSound(player.getLocation(), cancelSound.getSound(), cancelSound.getVolume(), cancelSound.getPitch());

            menu.getItems().forEach(item -> event.getPlayer().getInventory().addItem(item));
            menu.getUnsellableItems().forEach(item -> {
                // Ensure the item is still in the menu before trying to add it back, to avoid duplicating items if they were already moved by the player (if not cancelled).
                if (menu.getInventory().contains(item)) {
                    event.getPlayer().getInventory().addItem(item);
                }
            });
        }
    }
}
