package me.animepdf.fastsellcmi.menus;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Worth.WorthItem;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import me.animepdf.fastsellcmi.config.ConfigContainer;
import me.animepdf.fastsellcmi.config.SellMenuConfig;
import me.animepdf.fastsellcmi.services.PDCService;
import me.animepdf.fastsellcmi.utils.PlaceholderUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.Zrips.CMI.Modules.Economy.Economy.format;

public class FastSellMenu implements InventoryHolder {
    private final ConfigContainer configContainer;
    private final Inventory inventory;

    private final PDCService pdcService;

    @Getter
    private double totalPrice = 0.0;

    @Getter
    @Setter
    private boolean sold = false;
    @Getter
    @Setter
    private boolean transactionInProgress = false;

    FastSellMenu(ConfigContainer configContainer, PDCService pdcService) {
        this.configContainer = configContainer;
        this.pdcService = pdcService;
        this.inventory = Bukkit.createInventory(this, configContainer.getSellMenuConfig().inventoryMap.length * 9, configContainer.getSellMenuConfig().windowTitle);
        configureInventory();
        updateTotalPrice(true);
    }

    private void configureInventory() {
        SellMenuConfig menuConfig = configContainer.getSellMenuConfig();
        String[] layout = menuConfig.getInventoryMap();
        Map<Character, ItemStack> itemMap = menuConfig.itemMap;
        Map<Character, String> functionalMap = menuConfig.functionalMap;

        for (int i = 0; i < layout.length; i++) {
            char[] row = layout[i].toCharArray();
            for (int j = 0; j < row.length; j++) {
                char code = row[j];
                ItemStack sourceItem = itemMap.get(code);
                if (sourceItem == null || sourceItem.getType() == Material.AIR) {
                    continue;
                }

                ItemStack item = sourceItem.clone(); // Clone to avoid modifying the map's original item

                String buttonType = functionalMap.get(code);
                item = pdcService.applyButtonType(item, buttonType);
                inventory.setItem(i * 9 + j, item);
            }
        }
    }

    private void updatePriceButton() {
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];

            String buttonType = pdcService.getButtonType(item);
            if (buttonType == null || !buttonType.equals(SellMenuConfig.PRICE_BUTTON_TYPE)) continue;

            Component newName = PlaceholderUtils.replacePlaceholder(
                    configContainer.getSellMenuConfig().getItemForButtonType(SellMenuConfig.PRICE_BUTTON_TYPE).displayName(),
                    "{total}",
                    format(totalPrice)
            );

            item.editMeta(itemMeta -> itemMeta.displayName(newName));
            inventory.setItem(i, item);
        }
    }

    public void updateTotalPrice(boolean updateButton) {
        totalPrice = 0.0;
        getItems().forEach(itemStack -> {
            WorthItem worth = CMI.getInstance().getWorthManager().getWorth(itemStack);
            if (worth != null && worth.getSellPrice() > 0) {
                totalPrice += worth.getSellPrice() * itemStack.getAmount();
            }
        });

        if (updateButton) {
            updatePriceButton();
        }
    }

    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                items.add(item);
                continue;
            }

            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (!container.has(pdcService.getButtonTypeKey(), PersistentDataType.STRING)) {
                items.add(item);
            }
        }

        return items;
    }

    public List<ItemStack> getUnsellableItems() {
        return getItems().stream()
                .filter(item -> {
                    WorthItem worth = CMI.getInstance().getWorthManager().getWorth(item);
                    return worth == null || worth.getSellPrice() <= 0;
                })
                .collect(Collectors.toList());
    }

    public List<ItemStack> getSellableItems() {
        return getItems().stream()
                .filter(item -> {
                    WorthItem worth = CMI.getInstance().getWorthManager().getWorth(item);
                    return worth != null && worth.getSellPrice() > 0;
                })
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
