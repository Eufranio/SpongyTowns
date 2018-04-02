package io.github.eufranio.spongytowns;

import com.google.common.collect.Maps;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.spec.CommandSpec;

import java.util.Map;
import java.util.Optional;

/**
 * Created by Frani on 14/03/2018.
 */
public class BaseCommands {

    public static Map<String, CommandSpec> commands = Maps.newHashMap();
    public static Optional<CommandMapping> mapping = Optional.empty();

    public static void unregisterCommands() {
        mapping.ifPresent(Sponge.getCommandManager()::removeMapping);
        commands.clear();
    }

}
