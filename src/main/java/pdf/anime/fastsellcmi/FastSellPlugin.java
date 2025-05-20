package pdf.anime.fastsellcmi;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import pdf.anime.fastsellcmi.commands.FastSellCommand;
import pdf.anime.fastsellcmi.config.ConfigContainer;
import pdf.anime.fastsellcmi.listeners.FastSellMenuListener;
import pdf.anime.fastsellcmi.services.MenuService;
import pdf.anime.fastsellcmi.services.PDCService;
import pdf.anime.fastsellcmi.utils.BukkitRunner;

@Getter
public class FastSellPlugin extends JavaPlugin {

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

        getLogger().info("FastSellCMI enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (this.commandManager != null) {
            this.commandManager.unregisterCommands();
        }
        getLogger().info("FastSellCMI disabled.");
    }
}
