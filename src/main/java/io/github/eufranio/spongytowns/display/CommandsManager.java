package io.github.eufranio.spongytowns.display;

import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.commands.TownCommands;

/**
 * Created by Frani on 19/03/2018.
 */
public class CommandsManager {

    public static void registerCommands(SpongyTowns plugin) {
        TownCommands.registerCommands(plugin);
    }

}
