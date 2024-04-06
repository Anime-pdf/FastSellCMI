package pdf.anime.fastsellcmi;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteCommandsBukkit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pdf.anime.fastsellcmi.commands.FastSellCommand;
import pdf.anime.fastsellcmi.config.ConfigContainer;
import pdf.anime.fastsellcmi.listeners.FastSellMenuListener;
import pdf.anime.fastsellcmi.utils.BukkitRunner;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FastSellPlugin extends JavaPlugin {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private ConfigContainer configContainer;
    private LiteCommands<CommandSender> liteCommands;

    @Override
    public void onEnable() {
        configContainer = new ConfigContainer(getDataFolder());
        configContainer.loadConfigs();

        getServer().getPluginManager().registerEvents(new FastSellMenuListener(configContainer, new BukkitRunner(this)), this);

        this.liteCommands = LiteCommandsBukkit.builder("FastSellCMI", this)
                .commands(
                        new FastSellCommand(configContainer)
                )
                .build();
    }

    @Override
    public void onDisable() {
        if (this.liteCommands != null) {
            this.liteCommands.unregister();
        }
    }
}