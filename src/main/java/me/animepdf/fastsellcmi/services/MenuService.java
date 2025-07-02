package me.animepdf.fastsellcmi.services;

import org.bukkit.entity.Player;
import me.animepdf.fastsellcmi.config.ConfigContainer;
import me.animepdf.fastsellcmi.menus.FastSellMenu;
import me.animepdf.fastsellcmi.menus.FastSellMenuBuilder;

public class MenuService {
    private final ConfigContainer configContainer;
    private final PDCService pdcService;

    public MenuService(ConfigContainer configContainer, PDCService pdcService) {
        this.configContainer = configContainer;
        this.pdcService = pdcService;
    }

    public void openFastSellMenu(Player player) {
        FastSellMenu menu = new FastSellMenuBuilder()
                .configContainer(this.configContainer)
                .pdcService(this.pdcService)
                .build();
        player.openInventory(menu.getInventory());
    }
}
