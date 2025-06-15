package pdf.anime.fastsellcmi.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import pdf.anime.fastsellcmi.utils.SoundContainer;

@ConfigSerializable
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeneralConfig {
    public Component configReloaded = Component.text("FastSellCMI config reloaded!", NamedTextColor.GREEN);
    public Component missingPermission = Component.text("Not enough permissions!", NamedTextColor.RED);

    public Component sellMessage = Component.text("Items sold! You got {total}", NamedTextColor.GREEN);
    public Component noItemsSoldMessage = Component.text("No items could be sold.", NamedTextColor.YELLOW);
    public Component cancelMessage = Component.text("You canceled selling!", NamedTextColor.RED);

    public Component inventoryFullMessage = Component.text("Some items didn't fit in your inventory and were dropped at your feet.", NamedTextColor.YELLOW);

    public SoundContainer sellSound = new SoundContainer(Sound.ENTITY_VILLAGER_TRADE);
    public SoundContainer noItemsSoldSound = new SoundContainer(Sound.ENTITY_VILLAGER_HURT);
    public SoundContainer cancelSound = new SoundContainer(Sound.ENTITY_VILLAGER_NO);

    public SoundContainer inventoryFullSound = new SoundContainer(Sound.ENTITY_VILLAGER_DEATH);

    public GeneralConfig() {
    }
}
