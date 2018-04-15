package io.github.eufranio.spongytowns.commands;

import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.permission.Resident;
import io.github.eufranio.spongytowns.towns.Town;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Frani on 26/02/2018.
 */
public class Arguments {

    public static TownElement town(Text key) {
        return new TownElement(key);
    }

    public static PlotElement plot(Text key) {
        return new PlotElement(key);
    }

    public static ClaimElement claim(Text key) {
        return new ClaimElement(key);
    }

    public static ResidentElement resident(Text key) {
        return new ResidentElement(key);
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

    private static class PlotElement extends CommandElement {

        protected PlotElement(Text key) {
            super(key);
        }

        @Override
        protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
            return SpongyTowns.getManager()
                    .getPlot(args.next())
                    .orElseThrow(() -> args.createError(Text.of("That's not a valid Plot!")));
        }

        @Override
        public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
            return SpongyTowns.getManager().getPlots().values().stream().map(Claim::getName).collect(Collectors.toList());
        }

        @Override
        public Text getUsage(CommandSource src) {
            return Text.of("<plot name>");
        }
    }

    private static class ClaimElement extends CommandElement {

        protected ClaimElement(Text key) {
            super(key);
        }

        @Override
        protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
            String obj = args.next();
            if (obj.contains("@")) {
                String[] all = obj.split("@");
                try {
                    Map<UUID, Claim> map = null;
                    if (all[0].equalsIgnoreCase("town")) {
                        map = SpongyTowns.getManager().getTowns();
                    } else if (all[0].equalsIgnoreCase("plot")) {
                        map = SpongyTowns.getManager().getPlots();
                    } else {
                        throw args.createError(Text.of("Invalid input! Use town/plot@name!"));
                    }

                    Claim claim = map.values().stream()
                            .filter(c -> c.getName().equalsIgnoreCase(all[1]))
                            .findFirst()
                            .orElse(null);
                    if (claim == null) {
                        throw args.createError(Text.of("There's no claim with that name!"));
                    }
                    return claim;
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw args.createError(Text.of("Invalid input! Use <town/plot>@<name>!"));
                }
            } else {
                List<Claim> claims = SpongyTowns.getManager().getClaims().stream()
                        .filter(c -> c.getName().equalsIgnoreCase(obj))
                        .collect(Collectors.toList());
                if (claims.isEmpty()) {
                    throw args.createError(Text.of("There's no claim with that name!"));
                }
                if (claims.size() > 1) {
                    throw args.createError(Text.of("There are more than 1 claim with this name! Specify if it's a town or a plot using the following syntax: " +
                            "town/plot@name"));
                }
                return claims.get(0);
            }
        }

        @Override
        public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
            return SpongyTowns.getManager().getClaims().stream().map(Claim::getName).collect(Collectors.toList());
        }

        @Override
        public Text getUsage(CommandSource src) {
            return Text.of("<name>|<town/plot>@<name>");
        }
    }

    private static class ResidentElement extends CommandElement {

        protected ResidentElement(Text key) {
            super(key);
        }

        @Override
        protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
            String value = args.next();
            Resident res = SpongyTowns.getManager().getResidents().values()
                    .stream()
                    .filter(r -> r.getUser().getName().equalsIgnoreCase(value) ||
                            r.getUniqueId().toString().equalsIgnoreCase(value))
                    .findFirst()
                    .orElse(null);
            if (res == null) {
                throw args.createError(Text.of("That's not a valid Resident!"));
            }
            return res;
        }

        @Override
        public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
            return SpongyTowns.getManager().getResidents().values()
                    .stream()
                    .map(Resident::getUser)
                    .map(User::getName)
                    .collect(Collectors.toList());
        }

        @Override
        public Text getUsage(CommandSource src) {
            return Text.of("<resident>");
        }
    }

}
