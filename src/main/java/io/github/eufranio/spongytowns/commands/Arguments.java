package io.github.eufranio.spongytowns.commands;

import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.towns.Town;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Frani on 26/02/2018.
 */
public class Arguments {

    public static TownElement town(Text key) {
        return new TownElement(key);
    }

    private static class TownElement extends CommandElement {

        protected TownElement(Text key) {
            super(key);
        }

        @Override
        protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
            return SpongyTowns.getManager()
                    .getTown(args.next())
                    .orElseThrow(() -> args.createError(Text.of("That's not a valid Town!")));
        }

        @Override
        public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
            return SpongyTowns.getManager().getTowns().values().stream().map(Claim::getName).collect(Collectors.toList());
        }

        @Override
        public Text getUsage(CommandSource src) {
            return Text.of("<town name>");
        }
    }

}
