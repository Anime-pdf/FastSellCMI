package me.animepdf.fastsellcmi.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.animepdf.fastsellcmi.config.ConfigContainer;
import me.animepdf.fastsellcmi.services.MenuService;

@CommandAlias("fastsell|fsell")
public class FastSellCommand extends BaseCommand {
    private final ConfigContainer configContainer;
    private final MenuService menuService;

    public FastSellCommand(ConfigContainer configContainer, MenuService menuService) {
        this.configContainer = configContainer;
        this.menuService = menuService;
    }

    @Default
    @Description("Opens the fast sell menu.")
    public void onSell(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be run by a player.");
            return;
        }
        if (!player.hasPermission("fastsell.open")) {
            player.sendMessage(configContainer.getLanguageConfig().missingPermission);
            return;
        }
        menuService.openFastSellMenu(player);
    }

    @Subcommand("reload")
    @Description("Reloads the FastSellCMI configuration.")
    public void onReload(CommandSender sender) {
        if (!sender.hasPermission("fastsell.reload")) {
            sender.sendMessage(configContainer.getLanguageConfig().missingPermission);
            return;
        }
        configContainer.reloadConfigs();
        sender.sendMessage(configContainer.getLanguageConfig().configReloaded);
    }
}