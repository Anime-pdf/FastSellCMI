package pdf.anime.commands;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pdf.anime.config.Config;
import pdf.anime.gui.GuiSell;
import pdf.anime.utils.NamespacedKeysContainer;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommandSell implements CommandExecutor {

    Config config;
    NamespacedKeysContainer container;

    public CommandSell(Config config, NamespacedKeysContainer container) {
        this.config = config;
        this.container = container;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        if(args[0].equalsIgnoreCase("reload")) {
            if(!(sender instanceof Player player) || player.hasPermission("fastsell.reload")) {
                config.reload();
                sender.sendMessage(config.getAsColorful("reload-msg"));
            }
            return true;
        }


        if(sender instanceof Player player) {
            player.openInventory(new GuiSell(config, container).getInventory());
        }

        return true;
    }
}