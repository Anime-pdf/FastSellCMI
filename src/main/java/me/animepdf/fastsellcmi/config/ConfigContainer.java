package me.animepdf.fastsellcmi.config;

import lombok.Getter;
import me.animepdf.fastsellcmi.config.serializers.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import me.animepdf.fastsellcmi.utils.SoundContainer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public class ConfigContainer {
    private final File dataFolder;

    private GeneralConfig languageConfig;
    private SellMenuConfig sellMenuConfig;

    public ConfigContainer(File dataFolder) {
        this.dataFolder = dataFolder;
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    private <T> T loadConfiguration(Class<T> configClass, String fileName, TypeSerializerCollection serializers) {
        File configFile = new File(dataFolder, fileName);
        Path configPath = configFile.toPath();

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(configPath)
                .defaultOptions(opts -> opts.serializers(serializersInner -> serializersInner.registerAll(serializers)))
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .build();

        try {
            // Create file with default content if it doesn't exist
            if (Files.notExists(configPath)) {
                T newInstance = configClass.getDeclaredConstructor().newInstance();
                CommentedConfigurationNode newNode = loader.createNode();
                ObjectMapper.factory().get(configClass).save(newInstance, newNode);
                loader.save(newNode);
                return newInstance;
            }

            CommentedConfigurationNode node = loader.load();
            T configInstance = ObjectMapper.factory().get(configClass).load(node);

            ObjectMapper.factory().get(configClass).save(configInstance, node);
            loader.save(node);

            return configInstance;

        } catch (ConfigurateException error) {
            System.err.println("Error loading configuration: " + fileName);
            System.err.println("Error: " + error.getMessage());
            System.err.println("Error StackTrace: " + error.getMessage());
            error.printStackTrace();
            // Fallback: try to create a default instance if loading fails catastrophically
            try {
                System.err.println("Attempting to return a default instance for " + fileName);
                return configClass.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException roe) {
                throw new RuntimeException("Failed to instantiate default config for " + fileName, roe);
            }
        } catch (ReflectiveOperationException e) { // For getDeclaredConstructor().newInstance()
            throw new RuntimeException("Failed to create instance of config: " + fileName, e);
        }
    }

    public void loadConfigs() {
        TypeSerializerCollection generalSerializers = TypeSerializerCollection.builder()
                .register(Component.class, new ComponentSerializer())
                .register(SoundContainer.class, new SoundContainerSerializer())
                .build();
        languageConfig = loadConfiguration(GeneralConfig.class, "config.yml", generalSerializers);

        TypeSerializerCollection sellMenuSerializers = TypeSerializerCollection.builder()
                .register(Component.class, new ComponentSerializer())
                .register(Color.class, new ColorSerializer())
                .register(PotionEffect.class, new PotionEffectSerializer())
                .register(FireworkEffect.class, new FireworkEffectSerializer())
                .register(ItemStack.class, new SimpleItemStackSerializer())
                .build();
        sellMenuConfig = loadConfiguration(SellMenuConfig.class, "sell_menu.yml", sellMenuSerializers);
    }

    public void reloadConfigs() {
        loadConfigs();
    }
}