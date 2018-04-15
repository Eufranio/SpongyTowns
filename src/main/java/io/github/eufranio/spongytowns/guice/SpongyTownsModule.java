package io.github.eufranio.spongytowns.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.config.LastTaskRunConfig;
import io.github.eufranio.spongytowns.display.*;
import io.github.eufranio.spongytowns.managers.ConfigManager;
import io.github.eufranio.spongytowns.config.MainConfig;
import io.github.eufranio.spongytowns.config.MessagesCategory;
import io.github.eufranio.spongytowns.managers.StorageManager;

/**
 * Created by Frani on 19/03/2018.
 */
public class SpongyTownsModule extends AbstractModule {

    @Override
    public void configure() {
        // Empty impl of AbstractModule, needed by Guice
    }

    @Provides @Singleton
    public BankMessages provideBankMessages() {
        return SpongyTowns.getMessages().getBank();
    }

    @Provides @Singleton
    public EconomyMessages provideEconomyMessages() {
        return SpongyTowns.getMessages().getEconomy();
    }

    @Provides @Singleton
    public PermissionMessages providePermissionMessages() {
        return SpongyTowns.getMessages().getPermissions();
    }

    @Provides @Singleton
    public TownMessages provideTownMessages() {
        return SpongyTowns.getMessages().getTown();
    }

    @Provides @Singleton
    public LastTaskRunConfig provideLastTasks() {
        return SpongyTowns.getLastTasks();
    }

    @Provides @Singleton
    public ResidentMessages provideResidentMessages() {
        return SpongyTowns.getMessages().getRes();
    }

    @Provides @Singleton
    public ConfigManager<MainConfig> provideMainConfig() {
        return new ConfigManager<>(MainConfig.class,
                SpongyTowns.getInstance().getConfigDir(),
                "SpongyTowns.conf",
                SpongyTowns.getInstance().getFactory(),
                false,
                SpongyTowns.getInstance());
    }

    @Provides @Singleton
    public ConfigManager<MessagesCategory> provideMessages() {
        return new ConfigManager<>(MessagesCategory.class,
                SpongyTowns.getInstance().getConfigDir(),
                "Messages.conf",
                SpongyTowns.getInstance().getFactory(),
                false,
                SpongyTowns.getInstance());
    }

    @Provides @Singleton
    public ConfigManager<LastTaskRunConfig> provideTaskRunsConfig() {
        return new ConfigManager<>(LastTaskRunConfig.class,
                SpongyTowns.getInstance().getConfigDir(),
                "tasks.conf",
                SpongyTowns.getInstance().getFactory(),
                true,
                SpongyTowns.getInstance());
    }

    @Provides @Singleton
    public StorageManager provideStorageManager() {
        return new StorageManager(SpongyTowns.getConfig().getSqlURL());
    }

}
