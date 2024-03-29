package pdf.anime.fastsellcmi.config.serializers;


import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleItemStackSerializer implements TypeSerializer<ItemStack> {
    public SimpleItemStackSerializer() {
    }

    public ItemStack deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (!node.isNull() && !node.isList()) {
            if (node.isMap()) {
                node = (ConfigurationNode)node.childrenMap().get(node.childrenMap().keySet().toArray()[0]);
                String simpleItemData = (String)node.key();
                ItemStack item = this.deserializeSimple(simpleItemData);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.displayName((Component)node.node(new Object[]{"name"}).get(Component.class, Component.empty()));
                    meta.lore(node.node(new Object[]{"lore"}).getList(Component.class, new ArrayList()));
                    if (!node.node(new Object[]{"cmd"}).isNull()) {
                        meta.setCustomModelData(node.node(new Object[]{"cmd"}).getInt());
                    }

                    meta.setUnbreakable(node.node(new Object[]{"unbreakable"}).getBoolean(false));
                    this.deserializeDurability(node.node(new Object[]{"durability"}), meta, item.getType());
                    List list;
                    if (!node.node(new Object[]{"item_flags"}).isNull()) {
                        list = node.node(new Object[]{"item_flags"}).getList(String.class);
                        list.forEach((attribute) -> {
                            meta.addItemFlags(new ItemFlag[]{ItemFlag.valueOf(((String)attribute).toUpperCase())});
                        });
                    }

                    list = node.node(new Object[]{"enchantments"}).getList(String.class);
                    boolean isEnchantedBook = this.isEnchantmentBook(item);
                    if (list != null) {
                        list.forEach((s) -> {
                            String[] striped = ((String)s).split(" ");
                            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(striped[0]));
                            if (enchantment == null) {
                                throw new RuntimeException(striped[0] + " this enchantment not exist");
                            } else {
                                int level = Integer.parseInt(striped[1]);
                                if (!isEnchantedBook) {
                                    meta.addEnchant(enchantment, level, true);
                                } else {
                                    ((EnchantmentStorageMeta)meta).addStoredEnchant(enchantment, level, true);
                                }

                            }
                        });
                    }

                    this.deserializeColor(node.node(new Object[]{"color"}), meta);
                    this.deserializePotion(node.node(new Object[]{"potion"}), meta);
                    this.deserializeFirework(node.node(new Object[]{"firework"}), meta);
                    item.setItemMeta(meta);
                }

                return item;
            } else {
                return this.deserializeSimple(node.getString());
            }
        } else {
            return null;
        }
    }

    public void serialize(Type type, @Nullable ItemStack obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw((Object)null);
        } else {
            String simpleItemData = this.serializeSimple(obj);
            if (this.isSimple(obj)) {
                node.set(simpleItemData);
            } else {
                node = node.node(new Object[]{simpleItemData});
                ItemMeta meta = obj.getItemMeta();
                if (meta.hasDisplayName()) {
                    node.node(new Object[]{"name"}).set(meta.displayName());
                }

                if (meta.hasLore()) {
                    node.node(new Object[]{"lore"}).setList(Component.class, meta.lore());
                }

                if (meta.hasCustomModelData()) {
                    node.node(new Object[]{"cmd"}).set(meta.getCustomModelData());
                }

                if (meta.isUnbreakable()) {
                    node.node(new Object[]{"unbreakable"}).set(true);
                }

                List<String> serializedEnchantment = this.serializeEnchantments(obj);
                if (serializedEnchantment.size() != 0) {
                    node.node(new Object[]{"enchantments"}).setList(String.class, serializedEnchantment);
                }

                List<String> serializedItemFlag = new ArrayList();
                meta.getItemFlags().forEach((itemFlag) -> {
                    serializedItemFlag.add(itemFlag.name().toLowerCase());
                });
                if (serializedItemFlag.size() > 0) {
                    node.node(new Object[]{"item_flags"}).setList(String.class, serializedItemFlag);
                }

                this.serializeDurability(obj, node.node(new Object[]{"durability"}));
                this.serializePotion(obj, node.node(new Object[]{"potion"}));
                this.serializeFirework(obj, node.node(new Object[]{"firework"}));
                this.serializeColor(obj, node.node(new Object[]{"color"}));
            }
        }

    }

    private List<String> serializeEnchantments(ItemStack item) {
        List<String> serializedEnchantment = new ArrayList();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return serializedEnchantment;
        } else {
            Map enchantments;
            if (this.isEnchantmentBook(item)) {
                enchantments = ((EnchantmentStorageMeta)meta).getStoredEnchants();
            } else {
                enchantments = meta.getEnchants();
            }

            enchantments.forEach((enchantment, level) -> {
                String var10001 = ((Enchantment)enchantment).getKey().getKey();
                serializedEnchantment.add(var10001 + " " + level);
            });
            return serializedEnchantment;
        }
    }

    private void serializeDurability(ItemStack itemStack, ConfigurationNode node) throws SerializationException {
        if (this.isDamageable(itemStack)) {
            ItemMeta var4 = itemStack.getItemMeta();
            if (var4 instanceof Damageable) {
                Damageable damageable = (Damageable)var4;
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
            if (var4 instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta)var4;
                node.node(new Object[]{"base"}).set(potionMeta.getBasePotionData().getType().name());
                node.node(new Object[]{"effects"}).setList(PotionEffect.class, potionMeta.getCustomEffects());
            }
        }
    }

    private void deserializePotion(ConfigurationNode node, ItemMeta meta) throws SerializationException {
        if (meta != null) {
            if (meta instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta)meta;
                if (!node.isNull()) {
                    if (node.node(new Object[]{"base"}).get(String.class) != null) {
                        potionMeta.setBasePotionData(new PotionData(PotionType.valueOf((String)node.node(new Object[]{"base"}).get(String.class))));
                        node.node(new Object[]{"effects"}).getList(PotionEffect.class).forEach((effect) -> {
                            potionMeta.addCustomEffect(effect, false);
                        });
                    }
                }
            }
        }
    }

    private void deserializeDurability(ConfigurationNode node, ItemMeta meta, Material material) {
        if (meta != null) {
            if (meta instanceof Damageable) {
                Damageable damageable = (Damageable)meta;
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
            if (var4 instanceof FireworkMeta) {
                FireworkMeta meta = (FireworkMeta)var4;
                node.node(new Object[]{"effects"}).setList(FireworkEffect.class, meta.getEffects());
                node.node(new Object[]{"power"}).set(meta.getPower());
            }
        }
    }

    private void deserializeFirework(ConfigurationNode node, ItemMeta meta) throws SerializationException {
        if (meta != null) {
            if (meta instanceof FireworkMeta) {
                FireworkMeta fireworkMeta = (FireworkMeta)meta;
                if (!node.isNull()) {
                    fireworkMeta.setPower(node.node(new Object[]{"power"}).getInt());
                    fireworkMeta.addEffects(node.node(new Object[]{"effects"}).getList(FireworkEffect.class, new ArrayList()));
                }
            }
        }
    }

    private void deserializeColor(ConfigurationNode node, ItemMeta meta) throws SerializationException {
        if (!node.isNull()) {
            if (meta instanceof LeatherArmorMeta) {
                LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)meta;
                leatherArmorMeta.setColor((Color)node.get(Color.class));
            }
        }
    }

    private void serializeColor(ItemStack itemStack, ConfigurationNode node) throws SerializationException {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            if (meta instanceof LeatherArmorMeta) {
                LeatherArmorMeta armorMeta = (LeatherArmorMeta)meta;
                node.set(armorMeta.getColor());
            }
        }
    }

    private String serializeSimple(ItemStack itemStack) {
        String var10000 = itemStack.getType().name().toLowerCase();
        return var10000 + " " + itemStack.getAmount();
    }

    private ItemStack deserializeSimple(String simpleItemSerialized) {
        String[] stripped = simpleItemSerialized.split(" ");
        if (stripped.length != 2) {
            throw new RuntimeException("must be 2 parameters {material count}");
        } else {
            return new ItemStack(Material.getMaterial(stripped[0].toUpperCase()), Integer.parseInt(stripped[1]));
        }
    }

    private boolean isSimple(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return true;
        } else {
            return !meta.hasDisplayName() && !meta.hasLore() && !meta.hasCustomModelData() && !meta.hasEnchants() && meta.getItemFlags().size() <= 0 && !meta.isUnbreakable();
        }
    }

    private boolean isEnchantmentBook(ItemStack itemStack) {
        return itemStack.getItemMeta() != null && itemStack.getItemMeta() instanceof EnchantmentStorageMeta;
    }
}