package io.github.eufranio.spongytowns;

import com.google.inject.*;
import io.github.eufranio.spongytowns.config.LastTaskRunConfig;
import io.github.eufranio.spongytowns.guice.SpongyTownsModule;
import io.github.eufranio.spongytowns.managers.*;
import io.github.eufranio.spongytowns.config.MainConfig;
import io.github.eufranio.spongytowns.config.MessagesCategory;
import io.github.eufranio.spongytowns.permission.ClaimContextCalculator;
import io.github.eufranio.spongytowns.util.Util;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.permission.PermissionService;

import java.io.File;

@Singleton
@Plugin(
        id = "spongytowns",
        name = "SpongyTowns",
        description = "A WIP Towns plugin for Sponge",
        authors = {
                "Eufranio"
        }
)
public class SpongyTowns {

    @Inject
    @Getter
    private Logger logger;

    @Inject
    @Getter
    public GuiceObjectMapperFactory factory;

    @Inject
    @ConfigDir(sharedRoot = false)
    @Getter
    public File configDir;

    @Getter
    private Injector injector;

    @Getter
    public static Account SERVER_ACCOUNT;

    @Getter
    public static SpongyTowns instance;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        System.setProperty("com.j256.ormlite.logger.type", "LOCAL");
        System.setProperty("com.j256.ormlite.logger.level", "ERROR");
        injector = Guice.createInjector(new SpongyTownsModule());
        instance = this;
        Sponge.getServiceManager().provideUnchecked(PermissionService.class).registerContextCalculator(new ClaimContextCalculator());
        Managers.init();
        SERVER_ACCOUNT = getEconomyService().getOrCreateAccount(Util.SERVER_UUID).get();
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        getInjector().getInstance(Key.get(new TypeLiteral<ConfigManager<MainConfig>>(){})).load();
        getInjector().getInstance(Key.get(new TypeLiteral<ConfigManager<MessagesCategory>>(){})).load();
    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent e) {
        getStorage().saveTowns();
        getInstance().getInjector().getInstance(Key.get(new TypeLiteral<ConfigManager<LastTaskRunConfig>>(){})).cancel();
    }

    public static MainConfig getConfig() {
        return getInstance().getInjector().getInstance(Key.get(new TypeLiteral<ConfigManager<MainConfig>>(){})).getConfig();
    }

    public static MessagesCategory getMessages() {
        return getInstance().getInjector().getInstance(Key.get(new TypeLiteral<ConfigManager<MessagesCategory>>(){})).getConfig();
    }

    public static LastTaskRunConfig getLastTasks() {
        return getInstance().getInjector().getInstance(Key.get(new TypeLiteral<ConfigManager<LastTaskRunConfig>>(){})).getConfig();
    }

    public static boolean isEconomyEnabled() {
        return getConfig().isEconomyEnabled() && Sponge.getServiceManager().isRegistered(EconomyService.class);
    }

    public static EconomyService getEconomyService() {
        return Sponge.getServiceManager().provideUnchecked(EconomyService.class);
    }

    public static StorageManager getStorage() {
        return provide(StorageManager.class);
    }

    public static void log(String msg) {
        getInstance().logger.info(msg);
    }

    public static TownManager getManager() {
        return provide(TownManager.class);
    }

    // provides instances via Guice
    public static <T> T provide(Class<T> clazz) {
        return getInstance().injector.getInstance(clazz);
    }

}
