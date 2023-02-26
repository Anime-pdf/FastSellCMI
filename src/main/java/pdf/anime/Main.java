package pdf.anime;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pdf.anime.commands.CommandSell;
import pdf.anime.commands.CommandSellTabCompletion;
import pdf.anime.events.GuiEvents;

public class Main extends JavaPlugin {
    private static Main instance;
    public static FileConfiguration config;
    public void ReloadConfig()
    {
        reloadConfig();
        config = getConfig();
    }
    public static String GetColorStringConfig(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', config.getString(str));
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {
        instance = this;

        getCommand("fastsell").setExecutor(new CommandSell());
        getCommand("fastsell").setTabCompleter(new CommandSellTabCompletion());

        getServer().getPluginManager().registerEvents(new GuiEvents(), this);

        config = getConfig();
        config.addDefault("window-title", "Remote Seller");
        config.addDefault("reload-msg", "FastSell was successfully reloaded!");

        config.addDefault("sell-button", "&a&lSELL");
        config.addDefault("cancel-button", "&c&lCANCEL");
        config.addDefault("price-text", "&rSell for: &6&l{total}!");
        config.addDefault("sell-msg", "&bSuccessfully sold for &6&l{total}!");
        config.addDefault("sell-sound", "ENTITY_VILLAGER_CELEBRATE");

        config.options().copyDefaults(true);
        saveConfig();
    }

    public static Main getInstance() {
        return instance;
    }
}