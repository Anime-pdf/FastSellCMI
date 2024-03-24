package pdf.anime.fastsellcmi.commands;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pdf.anime.fastsellcmi.config.ConfigContainer;
import pdf.anime.fastsellcmi.menus.FastSellMenu;

@Command(name = "fastsell", aliases = "fsell")
public class FastSellCommand {
    private final ConfigContainer configContainer;

    public FastSellCommand(ConfigContainer configContainer) {
        this.configContainer = configContainer;
    }

    @Execute(name = "reload")
    public void reload(@Context CommandSender sender) {
        if(!sender.hasPermission("fastsell.reload")) {
            sender.sendMessage(configContainer.getLanguageConfig().missingPermission);
            return;
        }

        configContainer.reloadConfigs();
        sender.sendMessage(configContainer.getLanguageConfig().configReloaded);
    }

    @Execute
    public void sell(@Context CommandSender sender) {
        if(!sender.hasPermission("fastsell.sell")) {
            sender.sendMessage(configContainer.getLanguageConfig().missingPermission);
            return;
        }

        if (sender instanceof Player player) {
            player.openInventory(new FastSellMenu(configContainer).getInventory());
        }
    }
}
