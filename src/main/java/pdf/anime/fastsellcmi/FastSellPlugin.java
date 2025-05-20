package pdf.anime.fastsellcmi;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import pdf.anime.fastsellcmi.commands.FastSellCommand;
import pdf.anime.fastsellcmi.config.ConfigContainer;
import pdf.anime.fastsellcmi.listeners.FastSellMenuListener;
import pdf.anime.fastsellcmi.utils.BukkitRunner;

@Getter
public class FastSellPlugin extends JavaPlugin {

    private ConfigContainer configContainer;
    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        configContainer = new ConfigContainer(getDataFolder());
        configContainer.loadConfigs();

        getServer().getPluginManager().registerEvents(new FastSellMenuListener(configContainer, new BukkitRunner(this)), this);

        // Commands
        this.commandManager = new PaperCommandManager(this);
        this.commandManager.registerCommand(new FastSellCommand(this.configContainer, this.menuService));

        getLogger().info("FastSellCMI enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (this.commandManager != null) {
            this.commandManager.unregisterCommands();
        }
    }
}