package pdf.anime.config;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Config {
    MiniMessage miniMessage = MiniMessage.miniMessage();
    File configFile;
    FileConfiguration config;

    public Config(FileConfiguration config, String name) {
        this.configFile = new File(name);
        this.config = config;

        config.addDefault("window-title", "Remote Seller");
        config.addDefault("reload-msg", "<green>FastSell was successfully reloaded!");

        config.addDefault("sell-button", "<green>SELL");
        config.addDefault("cancel-button", "<red>CANCEL");
        config.addDefault("price-text", "<blue>Sell for: <red><total>!");
        config.addDefault("sell-msg", "<green>Successfully sold for <red><total>!");
        config.addDefault("sell-sound", "ENTITY_VILLAGER_CELEBRATE");

        config.options().copyDefaults(true);
        save();
    }

    public Component getAsColorful(String key) {
        String var;
        if((var = config.getString(key)) != null)
            return miniMessage.deserialize(var);
        return Component.empty();
    }
    public Component getAsColorful(String key, TagResolver.Single replacer) {
        String var;
        if((var = config.getString(key)) != null)
            return miniMessage.deserialize(var, replacer);
        return Component.empty();
    }
    public String get(String key) {
        return config.getString(key);
    }

    @SneakyThrows
    public void reload() {
        config.load(configFile);
    }

    @SneakyThrows
    public void save() {
        config.save(configFile);
    }

}
