package io.github.eufranio.spongytowns.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.config.LastTaskRunConfig;
import io.github.eufranio.spongytowns.managers.ConfigManager;
import io.github.eufranio.spongytowns.config.MainConfig;
import io.github.eufranio.spongytowns.config.MessagesCategory;
import io.github.eufranio.spongytowns.display.BankMessages;
import io.github.eufranio.spongytowns.display.EconomyMessages;
import io.github.eufranio.spongytowns.display.PermissionMessages;
import io.github.eufranio.spongytowns.display.TownMessages;
import io.github.eufranio.spongytowns.managers.StorageManager;

/**
 * Created by Frani on 19/03/2018.
 */
public class SpongyTownsModule extends AbstractModule {

    @Override
    public void configure() {
        // Empty impl of AbstractModule, needed by Guice
    }

    @Provides
    public BankMessages provideBankMessages() {
        return SpongyTowns.getMessages().getBank();
    }

    @Provides
    public EconomyMessages provideEconomyMessages() {
        return SpongyTowns.getMessages().getEconomy();
    }

    @Provides
    public PermissionMessages providePermissionMessages() {
        return SpongyTowns.getMessages().getPermissions();
    }

    @Provides
    public TownMessages provideTownMessages() {
        return SpongyTowns.getMessages().getTown();
    }

    @Provides
    public LastTaskRunConfig provideLastTasks() {
        return SpongyTowns.getLastTasks();
    }

    @Provides
    public ConfigManager<MainConfig> provideMainConfig() {
        return new ConfigManager<>(MainConfig.class,
                SpongyTowns.getInstance().getConfigDir(),
                "SpongyTowns.conf",
                SpongyTowns.getInstance().getFactory(),
                false,
                SpongyTowns.getInstance());
    }

    @Provides
    public ConfigManager<MessagesCategory> provideMessages() {
        return new ConfigManager<>(MessagesCategory.class,
                SpongyTowns.getInstance().getConfigDir(),
                "Messages.conf",
                SpongyTowns.getInstance().getFactory(),
                false,
                SpongyTowns.getInstance());
    }

    @Provides
    public ConfigManager<LastTaskRunConfig> provideTaskRunsConfig() {
        return new ConfigManager<>(LastTaskRunConfig.class,
                SpongyTowns.getInstance().getConfigDir(),
                "tasks.conf",
                SpongyTowns.getInstance().getFactory(),
                true,
                SpongyTowns.getInstance());
    }

    @Provides
    public StorageManager provideStorageManager() {
        return new StorageManager(SpongyTowns.getConfig().getSqlURL());
    }

}
