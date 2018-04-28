package io.github.eufranio.spongytowns.managers;

import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.commands.PlotCommands;
import io.github.eufranio.spongytowns.commands.TownCommands;

/**
 * Created by Frani on 19/03/2018.
 */
public class Managers {

    public static void init() {
        SpongyTowns.provide(StorageManager.class);
        ListenerManager.registerListeners(SpongyTowns.getInstance());
        TownCommands.registerCommands(SpongyTowns.getInstance());
        PlotCommands.registerCommands();
        SpongyTowns.provide(TaskManager.class).init(SpongyTowns.getInstance());
    }

}
