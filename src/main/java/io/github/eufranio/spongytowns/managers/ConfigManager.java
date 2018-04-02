package io.github.eufranio.spongytowns.managers;

import com.google.common.reflect.TypeToken;
import com.google.inject.Singleton;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import org.spongepowered.api.scheduler.Task;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Created by Frani on 27/01/2018.
 */
@Singleton
public class ConfigManager<T> {

    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private CommentedConfigurationNode config;
    private T node;
    private Class<T> configClass;
    private String file;
    private File configDir;
    private GuiceObjectMapperFactory mapper;
    private Task task;

    public ConfigManager(Class<T> configClass, File configDir, String file, GuiceObjectMapperFactory mapper, boolean autoSave, Object plugin) {
        this.configClass = configClass;
        this.file = file;
        this.mapper = mapper;
        this.configDir = configDir;
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        if (autoSave) {
            this.task = Task.builder()
                    .interval(60, TimeUnit.SECONDS)
                    .delayTicks(60)
                    .execute(this::reload)
                    .async()
                    .submit(plugin);
        }
        load();
    }

    @SuppressWarnings("unchecked")
    public void load() {
        try {
            File c = new File(configDir, file);
            if (!c.exists()) c.createNewFile();
            this.loader = HoconConfigurationLoader.builder().setFile(c).build();
            config = loader.load(ConfigurationOptions.defaults()
                    .setObjectMapperFactory(mapper)
                    .setShouldCopyDefaults(true));
            node = config.getValue(TypeToken.of(configClass), configClass.newInstance());
            loader.save(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public T getConfig() {
        return this.node;
    }

    @SuppressWarnings("unchecked")
    public void reload() {
        try {
            config.setValue(TypeToken.of(this.configClass), node);
            loader.save(config);
            load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        this.task.cancel();
        this.reload();
    }

}
