package pdf.anime.fastsellcmi.config;

import lombok.Getter;

import java.io.File;

@Getter
public class ConfigContainer {
    private final File dataFolder;

    private LanguageConfig languageConfig;
    private SellMenuConfig sellMenuConfig;

    public ConfigContainer(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    public void loadConfigs() {
        languageConfig = new LanguageConfig(new File(dataFolder, "lang.yml")).loadOrCreateConfig();
        sellMenuConfig = new SellMenuConfig(new File(dataFolder, "sell_menu.yml")).loadOrCreateConfig();
    }

    public void reloadConfigs() {
        languageConfig.reload();
        sellMenuConfig.reload();
    }
}
