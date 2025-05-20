package pdf.anime.fastsellcmi.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Map;

@ConfigSerializable
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SellMenuConfig {
    final static public String SELL_BUTTON_TYPE = "sell-button";
    final static public String CANCEL_BUTTON_TYPE = "cancel-button";
    final static public String PRICE_BUTTON_TYPE = "price-button";
    final static public String WALL_BUTTON_TYPE = "wall-button";

    public Component windowTitle = Component.text("Fast sell your items!", NamedTextColor.GREEN);
    public String[] inventoryMap = new String[]{
            "WWWWWWWWW",
            "W       W",
            "W       W",
            "W       W",
            "W       W",
            "WSSSPCCCW"
    };

    public Map<Character, ItemStack> itemMap;
    public Map<Character, String> functionalMap;

    public SellMenuConfig() {
        final ItemStack sellButtonItem;
        final ItemStack cancelButtonItem;
        final ItemStack priceButtonItem;
        final ItemStack wallButtonItem;

        { // sell button
            ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
            item.editMeta(itemMeta -> {
                itemMeta.displayName(Component.text("SELL", NamedTextColor.GREEN));
            });

            sellButtonItem = item;
        }
        { // cancel button
            ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            item.editMeta(itemMeta -> {
                itemMeta.displayName(Component.text("CANCEL", NamedTextColor.RED));
            });

            cancelButtonItem = item;
        }
        { // price button
            ItemStack item = new ItemStack(Material.END_CRYSTAL);
            item.editMeta(itemMeta -> {
                itemMeta.displayName(Component.text("Sell for: ", NamedTextColor.BLUE).append(Component.text("{total}", NamedTextColor.RED)));
            });

            priceButtonItem = item;
        }
        { // wall button
            ItemStack item = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
            item.editMeta(itemMeta -> {
                itemMeta.displayName(Component.text(" "));
            });

            wallButtonItem = item;
        }

        itemMap = Map.of('S', sellButtonItem, 'C', cancelButtonItem, 'P', priceButtonItem, 'W', wallButtonItem);
        functionalMap = Map.of('S', SELL_BUTTON_TYPE, 'C', CANCEL_BUTTON_TYPE, 'P', PRICE_BUTTON_TYPE, 'W', WALL_BUTTON_TYPE);
    }

    public Character getCharacterForButtonType(String buttonType) {
        if (buttonType == null || functionalMap == null) {
            return null;
        }
        for (Map.Entry<Character, String> entry : functionalMap.entrySet()) {
            if (buttonType.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
    public ItemStack getItemForCharacter(Character character) {
        if (character == null || itemMap == null) {
            return null;
        }
        for (Map.Entry<Character, ItemStack> entry : itemMap.entrySet()) {
            if (character.equals(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
    public ItemStack getItemForButtonType(String buttonType) {
        Character character = getCharacterForButtonType(buttonType);
        if (character == null) return null;
        return getItemForCharacter(character);
    }
}
