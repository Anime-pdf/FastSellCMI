package me.animepdf.fastsellcmi.menus;

import me.animepdf.fastsellcmi.config.ConfigContainer;
import me.animepdf.fastsellcmi.services.PDCService;

public class FastSellMenuBuilder {
    private ConfigContainer configContainer;
    private PDCService pdcService;

    public FastSellMenuBuilder configContainer(ConfigContainer configContainer) {
        this.configContainer = configContainer;
        return this;
    }

    public FastSellMenuBuilder pdcService(PDCService pdcService) {
        this.pdcService = pdcService;
        return this;
    }

    public FastSellMenu build() {
        if (configContainer == null || pdcService == null) {
            throw new IllegalStateException("ConfigContainer and PDCService must be set before building FastSellMenu.");
        }
        return new FastSellMenu(configContainer, pdcService);
    }
}
