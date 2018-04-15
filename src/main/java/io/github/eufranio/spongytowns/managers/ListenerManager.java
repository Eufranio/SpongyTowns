package io.github.eufranio.spongytowns.managers;

import com.google.common.collect.Lists;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.listeners.BlockEvents;
import io.github.eufranio.spongytowns.listeners.EntityEvents;
import io.github.eufranio.spongytowns.listeners.PlayerEvents;
import org.spongepowered.api.Sponge;

import java.util.List;

/**
 * Created by Frani on 06/03/2018.
 */
public class ListenerManager {

    public static void registerListeners(SpongyTowns plugin) {
        List<Object> listeners = Lists.newArrayList();

        listeners.add(SpongyTowns.provide(PlayerEvents.class));
        listeners.add(SpongyTowns.provide(BlockEvents.class));
        listeners.add(SpongyTowns.provide(EntityEvents.class));

        listeners.forEach(obj -> Sponge.getEventManager().registerListeners(plugin, obj));
    }

    public static void reloadListeners(SpongyTowns plugin) {
        Sponge.getEventManager().unregisterPluginListeners(plugin);
        Sponge.getEventManager().registerListeners(plugin, plugin);
        registerListeners(plugin);
    }

}
