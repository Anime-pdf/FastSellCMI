package pdf.anime.fastsellcmi.config;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import pdf.anime.fastsellcmi.config.serializers.ComponentSerializer;
import pdf.anime.fastsellcmi.config.serializers.SimpleItemStackSerializer;
import pdf.anime.fastsellcmi.config.utils.ConfigHolder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SellMenuConfig extends ConfigHolder<SellMenuConfig> {
    transient static public String SELL_BUTTON_TYPE = "sell-button";
    transient static public String CANCEL_BUTTON_TYPE = "cancel-button";
    transient static public String PRICE_BUTTON_TYPE = "price-button";
    transient static public String WALL_BUTTON_TYPE = "wall-button";

    transient static private ItemStack SELL_BUTTON_ITEM;
    transient static private ItemStack CANCEL_BUTTON_ITEM;
    transient static private ItemStack PRICE_BUTTON_ITEM;
    transient static private ItemStack WALL_BUTTON_ITEM;

    public Component windowTitle = Component.text("Fast sell you items!", NamedTextColor.GREEN);
    public String[] inventoryMap = new String[]{
            "WWWWWWWWW",
            "W       W",
            "W       W",
            "W       W",
            "W       W",
            "WSSSPCCCW"
    };

    /* public Map<Character, ItemStack> itemMap = new HashMap<>(){{
        put('S', SELL_BUTTON_ITEM);
        put('C', CANCEL_BUTTON_ITEM);
        put('P', PRICE_BUTTON_ITEM);
        put('W', WALL_BUTTON_ITEM);
    }}; */
    public BiMap<Character, ItemStack> itemMap = HashBiMap.create(Map.of(
            'S', SELL_BUTTON_ITEM,
            'C', CANCEL_BUTTON_ITEM,
            'P', PRICE_BUTTON_ITEM,
            'W', WALL_BUTTON_ITEM));

    /* public Map<Character, String> functionalMap = new HashMap<>() {{
        put('S', SELL_BUTTON_TYPE);
        put('C', CANCEL_BUTTON_TYPE);
        put('P', PRICE_BUTTON_TYPE);
        put('W', WALL_BUTTON_TYPE);
    }}; */

    public BiMap<Character, String> functionalMap = HashBiMap.create(Map.of(
            'S', SELL_BUTTON_TYPE,
            'C', CANCEL_BUTTON_TYPE,
            'P', PRICE_BUTTON_TYPE,
            'W', WALL_BUTTON_TYPE
    ));



    public SellMenuConfig(File baseFilePath) {
        super(baseFilePath, TypeSerializerCollection.builder()
                .register(Component.class, new ComponentSerializer())
                .register(ItemStack.class, new SimpleItemStackSerializer())
                .build());

        { // sell button
            ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
            item.editMeta(itemMeta -> {
                itemMeta.displayName(Component.text("SELL", NamedTextColor.GREEN));
            });

            SELL_BUTTON_ITEM = item;
        }
        { // cancel button
            ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            item.editMeta(itemMeta -> {
                itemMeta.displayName(Component.text("CANCEL", NamedTextColor.RED));
            });

            CANCEL_BUTTON_ITEM = item;
        }
        { // price button
            ItemStack item = new ItemStack(Material.END_CRYSTAL);
            item.editMeta(itemMeta -> {
                itemMeta.displayName(
                        Component.text("Sell for: ", NamedTextColor.BLUE)
                                .append(Component.text("{total}", NamedTextColor.RED))
                );
            });

            PRICE_BUTTON_ITEM = item;
        }
        { // wall button
            ItemStack item = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
            item.editMeta(itemMeta -> {
                itemMeta.displayName(Component.text(" "));
            });

            WALL_BUTTON_ITEM = item;
        }
    }

    public SellMenuConfig() {
        this(null);
    }
}
