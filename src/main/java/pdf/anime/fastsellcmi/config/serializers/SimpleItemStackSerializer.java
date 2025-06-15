package pdf.anime.fastsellcmi.config.serializers;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SimpleItemStackSerializer implements TypeSerializer<ItemStack> {

    public SimpleItemStackSerializer() {
    }

    public ItemStack deserialize(@NotNull Type type, ConfigurationNode node) throws SerializationException {
        if (node.isNull() || node.isList()) {
            return null;
        }

        if (!node.isMap()) {
            String simpleItem = node.getString();
            if (simpleItem == null || simpleItem.isBlank())
                return null;
            return this.deserializeSimple(simpleItem);
        }

        node = node.childrenMap().get(node.childrenMap().keySet().toArray()[0]);
        String simpleItemData = (String) node.key();
        ItemStack item = this.deserializeSimple(simpleItemData);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(node.node("name").get(Component.class, Component.empty()));
            meta.lore(node.node("lore").getList(Component.class, new ArrayList<>()));
            if (!node.node("cmd").isNull()) {
                meta.setCustomModelData(node.node("cmd").getInt());
            }

            meta.setUnbreakable(node.node("unbreakable").getBoolean(false));
            this.deserializeDurability(node.node("durability"), meta, item.getType());
            List<String> list;
            if (!node.node("item_flags").isNull()) {
                list = node.node("item_flags").getList(String.class, new ArrayList<>());
                list.forEach((attribute) -> meta.addItemFlags(ItemFlag.valueOf(attribute.toUpperCase())));
            }

            list = node.node("enchantments").getList(String.class);
            boolean isEnchantedBook = this.isEnchantmentBook(item);
            if (list != null) {
                list.forEach((s) -> {
                    String[] striped = s.split(" ");
                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(striped[0]));
                    if (enchantment == null) {
                        throw new RuntimeException(striped[0] + " this enchantment not exist");
                    } else {
                        int level = Integer.parseInt(striped[1]);
                        if (!isEnchantedBook) {
                            meta.addEnchant(enchantment, level, true);
                        } else {
                            ((EnchantmentStorageMeta) meta).addStoredEnchant(enchantment, level, true);
                        }
                    }
                });
            }

            this.deserializeColor(node.node("color"), meta);
            this.deserializePotion(node.node("potion"), meta);
            this.deserializeFirework(node.node("firework"), meta);
            this.deserializeSkull(node.node("textures"), meta);
            item.setItemMeta(meta);
        }

        return item;
    }

    public void serialize(@NotNull Type type, @Nullable ItemStack obj, @NotNull ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
        } else {
            String simpleItemData = this.serializeSimple(obj);
            if (this.isSimple(obj)) {
                node.set(simpleItemData);
            } else {
                node = node.node(simpleItemData);
                ItemMeta meta = obj.getItemMeta();
                if (meta.hasDisplayName()) {
                    node.node("name").set(meta.displayName());
                }

                if (meta.hasLore()) {
                    node.node("lore").setList(Component.class, meta.lore());
                }

                if (meta.hasCustomModelData()) {
                    node.node("cmd").set(meta.getCustomModelData());
                }

                if (meta.isUnbreakable()) {
                    node.node("unbreakable").set(true);
                }

                List<String> serializedEnchantment = this.serializeEnchantments(obj);
                if (!serializedEnchantment.isEmpty()) {
                    node.node("enchantments").setList(String.class, serializedEnchantment);
                }

                List<String> serializedItemFlag = new ArrayList<>();
                meta.getItemFlags().forEach((itemFlag) -> serializedItemFlag.add(itemFlag.name().toLowerCase()));
                if (!serializedItemFlag.isEmpty()) {
                    node.node("item_flags").setList(String.class, serializedItemFlag);
                }

                this.serializeDurability(obj, node.node("durability"));
                this.serializePotion(obj, node.node("potion"));
                this.serializeFirework(obj, node.node("firework"));
                this.serializeColor(obj, node.node("color"));
                this.serializeSkull(obj, node.node("textures"));
            }
        }
    }

    private List<String> serializeEnchantments(ItemStack item) {
        List<String> serializedEnchantment = new ArrayList<>();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return serializedEnchantment;
        } else {
            Map<Enchantment, Integer> enchantments;
            if (this.isEnchantmentBook(item)) {
                enchantments = ((EnchantmentStorageMeta) meta).getStoredEnchants();
            } else {
                enchantments = meta.getEnchants();
            }

            enchantments.forEach((enchantment, level) -> {
                String var10001 = enchantment.getKey().getKey();
                serializedEnchantment.add(var10001 + " " + level);
            });
            return serializedEnchantment;
        }
    }

    private void serializeDurability(ItemStack itemStack, ConfigurationNode node) throws SerializationException {
        if (this.isDamageable(itemStack)) {
            ItemMeta var4 = itemStack.getItemMeta();
            if (var4 instanceof Damageable damageable) {
                int maxDurability = itemStack.getType().getMaxDurability();
                if (damageable.getDamage() != 0) {
                    node.set(maxDurability - damageable.getDamage());
                }
            }
        }
    }

    private boolean isDamageable(ItemStack itemStack) {
        return itemStack.getItemMeta() != null && itemStack.getItemMeta() instanceof Damageable && itemStack.getType().getMaxDurability() != 0;
    }

    private void serializePotion(ItemStack itemStack, ConfigurationNode node) throws SerializationException {
        if (itemStack.getItemMeta() != null) {
            ItemMeta var4 = itemStack.getItemMeta();
            if (var4 instanceof PotionMeta potionMeta) {
                node.node("base").set(potionMeta.getBasePotionData().getType().name());
                node.node("effects").setList(PotionEffect.class, potionMeta.getCustomEffects());
            }
        }
    }

    private void deserializePotion(ConfigurationNode node, ItemMeta meta) throws SerializationException {
        if (meta != null) {
            if (meta instanceof PotionMeta potionMeta) {
                if (!node.isNull()) {
                    if (node.node("base").get(String.class) != null) {
                        potionMeta.setBasePotionData(new PotionData(PotionType.valueOf(node.node("base").get(String.class))));
                        node.node("effects").getList(PotionEffect.class, new ArrayList<>()).forEach((effect) -> potionMeta.addCustomEffect(effect, false));
                    }
                }
            }
        }
    }

    private void deserializeDurability(ConfigurationNode node, ItemMeta meta, Material material) {
        if (meta != null) {
            if (meta instanceof Damageable damageable) {
                if (!node.isNull()) {
                    int durability = node.getInt(-1);
                    if (durability != -1) {
                        int maxDurability = material.getMaxDurability();
                        damageable.setDamage(maxDurability - durability);
                    }

                }
            }
        }
    }

    private void serializeFirework(ItemStack itemStack, ConfigurationNode node) throws SerializationException {
        if (itemStack.getItemMeta() != null) {
            ItemMeta var4 = itemStack.getItemMeta();
            if (var4 instanceof FireworkMeta meta) {
                node.node("effects").setList(FireworkEffect.class, meta.getEffects());
                node.node("power").set(meta.getPower());
            }
        }
    }

    private void deserializeFirework(ConfigurationNode node, ItemMeta meta) throws SerializationException {
        if (meta != null) {
            if (meta instanceof FireworkMeta fireworkMeta) {
                if (!node.isNull()) {
                    fireworkMeta.setPower(node.node("power").getInt());
                    fireworkMeta.addEffects(node.node("effects").getList(FireworkEffect.class, new ArrayList<>()));
                }
            }
        }
    }

    private void deserializeColor(ConfigurationNode node, ItemMeta meta) throws SerializationException {
        if (!node.isNull()) {
            if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
                leatherArmorMeta.setColor(node.get(Color.class));
            }
        }
    }

    private void serializeColor(ItemStack itemStack, ConfigurationNode node) throws SerializationException {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            if (meta instanceof LeatherArmorMeta armorMeta) {
                node.set(armorMeta.getColor());
            }
        }
    }

    private void serializeSkull(ItemStack itemStack, ConfigurationNode node) throws SerializationException {
        if (itemStack.getItemMeta() instanceof SkullMeta meta) {
            PlayerProfile profile = meta.getPlayerProfile();
            if (profile == null || !profile.hasTextures()) return;
            node.set(profile.getProperties().stream()
                    .filter(property -> "textures".equals(property.getName())).findAny().orElseThrow().getValue());
        }
    }

    private void deserializeSkull(ConfigurationNode node, ItemMeta meta) throws SerializationException {
        if (!node.isNull()) {
            if (meta instanceof SkullMeta skull) {
                String textures = node.require(String.class);
                PlayerProfile profile = Bukkit.createProfile(new UUID(textures.hashCode(), textures.hashCode()));
                profile.setProperty(new ProfileProperty("textures", textures));
                skull.setPlayerProfile(profile);
            }
        }
    }

    private String serializeSimple(ItemStack itemStack) {
        String type = itemStack.getType().name().toLowerCase();
        return type + " " + itemStack.getAmount();
    }

    private ItemStack deserializeSimple(String simpleItemSerialized) {
        String[] stripped = simpleItemSerialized.split(" ");
        if (stripped.length != 2) {
            throw new RuntimeException("must be 2 parameters {material count}");
        } else {
            int amount = 1;
            Material material = Material.getMaterial(stripped[0]);
            if (material == null) {
                return null;
            }
            try {
                amount = Integer.parseInt(stripped[1]);
            } catch (NumberFormatException e) {
                return null;
            }
            return new ItemStack(material, amount);
        }
    }

    private boolean isSimple(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        boolean complicated = false;
        if (meta == null) {
            return true;
        }

        if (meta instanceof Damageable damageable) {
            complicated = damageable.hasDamage();
        }

        complicated = complicated ||
                (meta instanceof PotionMeta) ||
                (meta instanceof FireworkMeta) ||
                (meta instanceof LeatherArmorMeta) ||
                (meta instanceof SkullMeta) ||
                (meta instanceof EnchantmentStorageMeta) ||
                meta.hasDisplayName() ||
                meta.hasLore() ||
                meta.hasCustomModelData() ||
                meta.hasEnchants() ||
                !meta.getItemFlags().isEmpty() ||
                meta.isUnbreakable();
        return !complicated;
    }

    private boolean isEnchantmentBook(ItemStack itemStack) {
        return itemStack.getItemMeta() != null && itemStack.getItemMeta() instanceof EnchantmentStorageMeta;
    }
}