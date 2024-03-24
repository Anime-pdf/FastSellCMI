package pdf.anime.fastsellcmi.menus;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Worth.WorthItem;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pdf.anime.fastsellcmi.config.ConfigContainer;
import pdf.anime.fastsellcmi.config.SellMenuConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.Zrips.CMI.Modules.Economy.Economy.format;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class FastSellMenu implements InventoryHolder {
    final ConfigContainer configContainer;
    final Inventory inventory;

    @Getter
    double totalPrice = 0f;

    @Setter
    @Getter
    boolean sold = false;

    public FastSellMenu(ConfigContainer configContainer) {
        this.configContainer = configContainer;
        this.inventory = Bukkit.createInventory(this, 9*6, configContainer.getSellMenuConfig().windowTitle);
        configure();
        updateTotalPrice(true);
    }

    public void configure() {
        String[] inventoryMap = configContainer.getSellMenuConfig().inventoryMap;
        Map<Character, ItemStack> itemMap = configContainer.getSellMenuConfig().itemMap;
        Map<Character, String> functionalMap = configContainer.getSellMenuConfig().functionalMap;
        char[] charString;

        for (int i = 0; i < inventoryMap.length; i++) {
            charString = inventoryMap[i].toCharArray();
            for (int j = 0; j < charString.length; j++) {
                char code = charString[j];
                ItemStack itemStack = itemMap.get(code);
                if(itemStack == null || itemStack.getAmount() == 0 || itemStack.getType() == Material.AIR) {
                    continue;
                }

                NBT.modify(itemStack, nbt -> {
                    nbt.setString("button-type", functionalMap.get(code));
                });
                inventory.setItem(i*9+j, itemStack);
            }
        }
    }

    public void updateTotalPriceButton() {
        Arrays.stream(inventory.getContents()).filter(Objects::nonNull).filter(itemStack -> {
            NBTItem nbtItem = new NBTItem(itemStack);
            String type = nbtItem.getString("button-type");
            if(type == null || type.isEmpty()) {
                return false;
            }

            return type.equals(SellMenuConfig.PRICE_BUTTON_TYPE);
        }).forEach(itemStack -> itemStack.editMeta(itemMeta -> {
            Character chr = getKey(configContainer.getSellMenuConfig().functionalMap, SellMenuConfig.PRICE_BUTTON_TYPE);
            if(chr == null) {
                return;
            }

            ItemMeta meta = configContainer.getSellMenuConfig().itemMap.get(chr).getItemMeta();

            if(meta == null) {
                return;
            }
            Component oldName = meta.displayName();
            if(oldName == null) {
                return;
            }

            itemMeta.displayName(oldName.replaceText(TextReplacementConfig.builder().match(Pattern.compile("\\{total\\}")).replacement(format(totalPrice)).build()).asComponent());
        }));
    }

    public void updateTotalPrice(boolean updateButton) {
        totalPrice = 0f;
        getItems().forEach(itemStack -> {
            WorthItem worth = CMI.getInstance().getWorthManager().getWorth(itemStack);
            if (worth != null && worth.getSellPrice() > 0){
                totalPrice += worth.getSellPrice() * itemStack.getAmount();
            }
        });

        if(updateButton) {
            updateTotalPriceButton();
        }
    }

    public List<ItemStack> getItems() {
        return Arrays.stream(inventory.getContents()).filter(Objects::nonNull).filter(itemStack -> {
            NBTItem nbtItem = new NBTItem(itemStack);
            String type = nbtItem.getString("button-type");
            if(type == null || type.isEmpty()) {
                return true;
            }

            return !type.equals(SellMenuConfig.SELL_BUTTON_TYPE) &&
                    !type.equals(SellMenuConfig.CANCEL_BUTTON_TYPE) &&
                    !type.equals(SellMenuConfig.PRICE_BUTTON_TYPE) &&
                    !type.equals(SellMenuConfig.WALL_BUTTON_TYPE);
        }).toList();
    }

    public List<ItemStack> getUnsellableItems() {
        return Arrays.stream(inventory.getContents()).filter(Objects::nonNull).filter(itemStack -> {
            NBTItem nbtItem = new NBTItem(itemStack);
            if(!nbtItem.hasTag("button-type")) {
                WorthItem worth = CMI.getInstance().getWorthManager().getWorth(itemStack);
                return worth == null || worth.getSellPrice() <= 0;
            }

            return false;
        }).toList();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    private <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
