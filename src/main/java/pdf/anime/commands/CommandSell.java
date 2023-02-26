package pdf.anime.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pdf.anime.Main;
import pdf.anime.gui.GuiSell;

public class CommandSell implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("fastsell.reload")) {
                Main.getInstance().ReloadConfig();
                sender.sendMessage(Main.GetColorStringConfig("reload-msg"));
            }
        }
        else {
            if (sender instanceof Player) {
                ((Player) sender).openInventory(new GuiSell().getInventory());
            }
        }
        return true;
    }
}