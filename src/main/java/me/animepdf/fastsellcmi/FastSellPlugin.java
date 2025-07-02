package me.animepdf.fastsellcmi;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import me.animepdf.fastsellcmi.commands.FastSellCommand;
import me.animepdf.fastsellcmi.config.ConfigContainer;
import me.animepdf.fastsellcmi.listeners.FastSellMenuListener;
import me.animepdf.fastsellcmi.services.MenuService;
import me.animepdf.fastsellcmi.services.PDCService;
import me.animepdf.fastsellcmi.utils.BukkitRunner;
import me.animepdf.fastsellcmi.bstats.Metrics;

@Getter
public class FastSellPlugin extends JavaPlugin {
    private Metrics metrics;

    private ConfigContainer configContainer;
    private PaperCommandManager commandManager;

    private MenuService menuService;
    private PDCService pdcService;
    private BukkitRunner runnerService;

    @Override
    public void onEnable() {
        // Services
        this.runnerService = new BukkitRunner(this);
        this.pdcService = new PDCService(this);
        this.configContainer = new ConfigContainer(getDataFolder());
        this.configContainer.loadConfigs();
        this.menuService = new MenuService(this.configContainer, this.pdcService);

        // Listeners
        getServer().getPluginManager().registerEvents(new FastSellMenuListener(this.configContainer, this.pdcService, this.runnerService), this);

        // Commands
        this.commandManager = new PaperCommandManager(this);
        this.commandManager.registerCommand(new FastSellCommand(this.configContainer, this.menuService));

        this.metrics = new Metrics(this, 21511);

        getLogger().info("FastSellCMI enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (this.commandManager != null) {
            this.commandManager.unregisterCommands();
        }
        if(this.metrics != null) {
            this.metrics.shutdown();
        }
        getLogger().info("FastSellCMI disabled.");
    }
}
