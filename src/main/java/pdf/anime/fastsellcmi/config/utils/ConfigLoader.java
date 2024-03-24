package pdf.anime.fastsellcmi.config.utils;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

public class ConfigLoader {
    private final File baseFilePath;
    private final TypeSerializerCollection[] serializers;
    private ConfigHolder config;

    public ConfigLoader(File baseFilePath, ConfigHolder config, TypeSerializerCollection... serializers) {
        this.baseFilePath = baseFilePath;
        this.serializers = serializers;
        this.config = config;
    }

    public <T extends ConfigHolder> T load(Class<T> type) {
        return this.load(type, true);
    }

    public <T extends ConfigHolder> T load(Class<T> type, boolean save) {
        return this.load(this.baseFilePath, type, save, this.serializers);
    }

    public <T extends ConfigHolder> T load(String path, Class<T> type) {
        File file = new File(this.baseFilePath, path);
        return this.load(file, type, true, this.serializers);
    }

    public <T extends ConfigHolder> T load(File file, Class<T> type, boolean save, TypeSerializerCollection... serializers) {
        try {
            YamlConfigurationLoader loader = this.getLoader(file, serializers);
            CommentedConfigurationNode node = (CommentedConfigurationNode)loader.load();
            T config = node.get(type);
            config.setRootNode(node);
            config.onPostLoad();
            if (save) {
                node.set(type, config);

                try {
                    loader.save(node);
                } catch (ConfigurateException var9) {
                    var9.printStackTrace();
                }
            }

            return config;
        } catch (Exception var10) {
            throw new RuntimeException(var10);
        }
    }

    public void save() {
        this.save(this.baseFilePath, this.config, this.serializers);
    }

    public void save(String path) {
        File file = new File(this.baseFilePath, path);
        this.save(file, this.config, this.serializers);
    }

    public void save(File file, Object obj, TypeSerializerCollection... serializers) {
        try {
            YamlConfigurationLoader loader = this.getLoader(file, serializers);
            CommentedConfigurationNode node = (CommentedConfigurationNode)loader.load();
            node.set(obj.getClass(), obj);
            loader.save(node);
        } catch (Exception var6) {
            throw new RuntimeException(var6);
        }
    }

    private YamlConfigurationLoader getLoader(File file, TypeSerializerCollection... serializers) {
        YamlConfigurationLoader.Builder builder = (YamlConfigurationLoader.Builder)YamlConfigurationLoader.builder().indent(2).nodeStyle(NodeStyle.BLOCK).file(file);
        if (serializers.length > 0) {
            builder.defaultOptions((opts) -> {
                return opts.serializers((b) -> {
                    TypeSerializerCollection[] var2 = serializers;
                    int var3 = serializers.length;

                    for(int var4 = 0; var4 < var3; ++var4) {
                        TypeSerializerCollection collection = var2[var4];
                        b.registerAll(collection);
                    }

                });
            });
        }

        return builder.build();
    }

    public void setConfig(ConfigHolder config) {
        this.config = config;
    }
}
