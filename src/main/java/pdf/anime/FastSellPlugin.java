package pdf.anime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.java.JavaPlugin;
import pdf.anime.commands.CommandSell;
import pdf.anime.commands.CommandSellTabCompletion;
import pdf.anime.config.Config;
import pdf.anime.events.GuiEvents;
import pdf.anime.utils.BukkitRunner;
import pdf.anime.utils.NamespacedKeysContainer;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FastSellPlugin extends JavaPlugin {
    Config mainConfig;
    NamespacedKeysContainer container;
    BukkitRunner runner;

    @Override
    public void onEnable() {
        mainConfig = new Config(getConfig(), "config.yml");
        container = new NamespacedKeysContainer(this);
        runner = new BukkitRunner(this);

        getCommand("fastsell").setExecutor(new CommandSell(mainConfig, container));
        getCommand("fastsell").setTabCompleter(new CommandSellTabCompletion());

        getServer().getPluginManager().registerEvents(new GuiEvents(mainConfig, container, runner), this);
    }
}