package pdf.anime.fastsellcmi.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import pdf.anime.fastsellcmi.config.serializers.ComponentSerializer;
import pdf.anime.fastsellcmi.config.utils.ConfigHolder;

import java.io.File;

@ConfigSerializable
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeneralConfig extends ConfigHolder<GeneralConfig> {
    public Component configReloaded = Component.text("FastSellCMI config reloaded!", NamedTextColor.GREEN);
    public Component missingPermission = Component.text("Not enough permissions!", NamedTextColor.RED);

    public Component sellMessage = Component.text("Items sold! You got {total}", NamedTextColor.GREEN);
    public Component cancelMessage = Component.text("You canceled selling!", NamedTextColor.RED);

    public String sellSound = Sound.ENTITY_VILLAGER_TRADE.name();
    public String cancelSound = Sound.ENTITY_VILLAGER_NO.name();

    public GeneralConfig(File baseFilePath) {
        super(baseFilePath, TypeSerializerCollection.builder()
                .register(Component.class, new ComponentSerializer())
                .build());
    }

    public GeneralConfig() {
        this(null);
    }
}
