package pdf.anime.fastsellcmi.config;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import pdf.anime.fastsellcmi.config.serializers.*;
import pdf.anime.fastsellcmi.config.utils.ConfigHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public BiMap<Character, ItemStack> itemMap;
    public BiMap<Character, String> functionalMap;

    public ItemStack general_example;
    public ItemStack potion_example;
    public ItemStack firework_example;
    public ItemStack color_example;
    public ItemStack skull_example;

    public SellMenuConfig(File baseFilePath) {
        super(baseFilePath, TypeSerializerCollection.builder()
                .register(Component.class, new ComponentSerializer())
                .register(Color.class, new ColorSerializer())
                .register(PotionEffect.class, new PotionEffectSerializer())
                .register(FireworkEffect.class, new FireworkEffectSerializer())
                .register(ItemStack.class, new SimpleItemStackSerializer())
                .build());

        // examples
        general_example = new ItemStack(Material.DIAMOND_SWORD);
        general_example.editMeta(Damageable.class, itemMeta -> {
            itemMeta.displayName(Component.text("Green name", NamedTextColor.GREEN));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Lore 1"));
            lore.add(Component.text("Lore 2"));
            lore.add(Component.text("Lore 3"));
            itemMeta.lore(lore);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_DYE);
            itemMeta.addEnchant(Enchantment.CHANNELING, 10, true);
            itemMeta.setCustomModelData(6666);
            itemMeta.setUnbreakable(true);
            itemMeta.setDamage(general_example.getType().getMaxDurability()/2);
        });

        potion_example = new ItemStack(Material.POTION);
        potion_example.editMeta(PotionMeta.class, itemMeta -> {
            itemMeta.setBasePotionData(new PotionData(PotionType.AWKWARD));
            itemMeta.addCustomEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 1, true), true);
        });

        firework_example = new ItemStack(Material.FIREWORK_ROCKET);
        firework_example.editMeta(FireworkMeta.class, itemMeta -> {
            FireworkEffect.Builder fe = FireworkEffect.builder();
            fe.withColor(Color.GRAY);
            fe.withFlicker();
            fe.withTrail();
            fe.withFade(Color.AQUA, Color.RED, Color.GREEN);
            itemMeta.addEffects(fe.build());
        });

        color_example = new ItemStack(Material.LEATHER_CHESTPLATE);
        color_example.editMeta(LeatherArmorMeta.class, itemMeta -> {
            itemMeta.setColor(Color.AQUA);
        });

        skull_example = new ItemStack(Material.PLAYER_HEAD);
        skull_example.editMeta(SkullMeta.class, itemMeta -> {
            itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("e9bc192b-f761-4de6-b31f-6a36b3c9f494")));
        });

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

        itemMap = HashBiMap.create(Map.of(
                'S', SELL_BUTTON_ITEM,
                'C', CANCEL_BUTTON_ITEM,
                'P', PRICE_BUTTON_ITEM,
                'W', WALL_BUTTON_ITEM
        ));

        functionalMap = HashBiMap.create(Map.of(
                'S', SELL_BUTTON_TYPE,
                'C', CANCEL_BUTTON_TYPE,
                'P', PRICE_BUTTON_TYPE,
                'W', WALL_BUTTON_TYPE
        ));
    }

    public SellMenuConfig() {
        this(null);
    }
}
