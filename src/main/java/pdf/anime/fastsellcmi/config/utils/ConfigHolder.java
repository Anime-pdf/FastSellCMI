package pdf.anime.fastsellcmi.config.utils;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ConfigSerializable
public abstract class ConfigHolder<T extends ConfigHolder<T>> {
    protected transient ConfigLoader loader;
    protected transient File baseFilePath;
    protected transient CommentedConfigurationNode rootNode;

    public ConfigHolder(File baseFilePath) {
        this(baseFilePath, TypeSerializerCollection.builder().build());
    }

    public ConfigHolder(File baseFilePath, TypeSerializerCollection... serializers) {
        this.loader = new ConfigLoader(baseFilePath, this, serializers);
        this.baseFilePath = baseFilePath;
    }

    public void setRootNode(CommentedConfigurationNode rootNode) {
        this.rootNode = rootNode;
    }

    public void onPostLoad() {
    }

    /** @deprecated */
    @Deprecated
    public T load() {
        return this.load(true);
    }

    public T loadAndSave() {
        return this.load(true);
    }

    public T load(boolean save) {
        this.validateEmptyConstructor();
        this.validateAnnotation();
        T config = this.loader.load(this.getGenericClass(), save);
        this.loader.setConfig(config);
        config.setLoader(this.loader);
        config.baseFilePath = this.baseFilePath;
        this.setFieldsOnLoad(config);
        return config;
    }

    private void validateAnnotation() {
        Class<T> clazz = this.getGenericClass();
        Class<?> annotationClass = ConfigSerializable.class;
        boolean containsConfigSerializableAnnotation = Arrays.stream(clazz.getAnnotations()).anyMatch((annotation) -> {
            return annotation.annotationType() == annotationClass;
        });
        if (!containsConfigSerializableAnnotation) {
            String var10002 = clazz.getName();
            throw new RuntimeException("class " + var10002 + " don`t have " + annotationClass.getName() + " annotation");
        }
    }

    private void validateEmptyConstructor() {
        Class<T> clazz = this.getGenericClass();
        boolean emptyConstructorExist = Arrays.stream(clazz.getConstructors()).anyMatch((constructor) -> {
            return constructor.getParameterCount() == 0;
        });
        if (!emptyConstructorExist) {
            emptyConstructorExist = Arrays.stream(clazz.getDeclaredConstructors()).anyMatch((constructor) -> {
                return constructor.getParameterCount() == 0;
            });
        }

        if (!emptyConstructorExist) {
            throw new RuntimeException("class " + clazz.getName() + " don`t have empty constructor");
        }
    }

    public Class<T> getGenericClass() {
        Class<?> actual = this.getClass();
        ParameterizedType type = (ParameterizedType)actual.getGenericSuperclass();
        return (Class)type.getActualTypeArguments()[0];
    }

    public void setFields(T other, boolean ignoreTransient, boolean onlyPublic) {
        Class<T> clazz = this.getGenericClass();
        Set<Field> fields = new HashSet(Arrays.stream(clazz.getFields()).toList());
        if (!onlyPublic) {
            fields.addAll(Arrays.stream(clazz.getDeclaredFields()).peek((field) -> {
                field.setAccessible(true);
            }).toList());
        }

        fields.stream().filter((field) -> {
            if (!ignoreTransient) {
                return true;
            } else {
                return !Modifier.isTransient(field.getModifiers());
            }
        }).forEach((field) -> {
            try {
                Object otherObject = field.get(other);
                field.set(this, otherObject);
            } catch (Exception var4) {
                throw new RuntimeException(var4);
            }
        });
    }

    public File getBaseFilePath() {
        return this.baseFilePath;
    }

    public void reload() {
        this.setFields(this.load(false), false, false);
    }

    public void save() {
        this.loader.save();
    }

    public void setLoader(ConfigLoader loader) {
        this.loader = loader;
    }

    protected void setFieldsOnLoad(T newConfig) {
    }

    public T loadOrCreateConfig() {
        return this.baseFilePath.exists() ? this.load(false) : this.loadAndSave();
    }
}
