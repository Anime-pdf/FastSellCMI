package pdf.anime.fastsellcmi;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.adventure.LiteAdventureExtension;
import dev.rollczi.litecommands.bukkit.LiteBukkitMessages;
import dev.rollczi.litecommands.bukkit.LiteCommandsBukkit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pdf.anime.fastsellcmi.commands.FastSellCommand;
import pdf.anime.fastsellcmi.config.ConfigContainer;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FastSellPlugin extends JavaPlugin {
    private ConfigContainer configContainer;

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private LiteCommands<CommandSender> liteCommands;

    @Override
    public void onEnable() {
        configContainer = new ConfigContainer(getDataFolder());
        configContainer.loadConfigs();

        this.liteCommands = LiteCommandsBukkit.builder("FastSellCMI", this)
                .extension(new LiteAdventureExtension<>(), configuration -> configuration
                        .miniMessage(true)
                        .legacyColor(true)
                        .colorizeArgument(true)
                        .serializer(this.miniMessage)
                )
                .commands(
                        new FastSellCommand(configContainer)
                )
                .message(LiteBukkitMessages.MISSING_PERMISSIONS, input -> MiniMessage.miniMessage().serialize(configContainer.getLanguageConfig().getMissingPermission()))
                .message(LiteBukkitMessages.PLAYER_ONLY, "<red>Only player can execute this command!")

                .build();
    }

    @Override
    public void onDisable() {
        if (this.liteCommands != null) {
            this.liteCommands.unregister();
        }
    }
}