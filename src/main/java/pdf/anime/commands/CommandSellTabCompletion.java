package pdf.anime.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandSellTabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, @NotNull Command cmd, @NotNull String arg, String[] args) {
        List<String> list = new ArrayList<>();
        if(sender.hasPermission("fastsell.reload") && args.length == 1)
            list.add("reload");
        return list;
    }
}