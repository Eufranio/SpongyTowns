package io.github.eufranio.spongytowns.permission;

import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.towns.PlotClaim;
import io.github.eufranio.spongytowns.towns.TownClaim;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.Subject;

import java.util.Optional;
import java.util.Set;

/**
 * Created by Frani on 24/02/2018.
 */
public class ClaimContextCalculator implements ContextCalculator<Subject> {

    public static final Context IN_CLAIM = new Context("spongytowns-inClaim", "true");
    public static final Context NOT_IN_CLAIM = new Context("spongytowns-inClaim", "false");
    public static final String TOWN = "spongytowns-town";
    public static final String PLOT = "spongytowns-plot";

    @Override
    public void accumulateContexts(Subject calculable, Set<Context> accumulator) {
        final Optional<CommandSource> commandSource = calculable.getCommandSource();
        if (commandSource.isPresent() && commandSource.get() instanceof Player) {
            final Player player = (Player) commandSource.get();
            Optional<TownClaim> townClaim = SpongyTowns.getManager().getClaimAt(player.getLocation());
            if (townClaim.isPresent()) {
                accumulator.add(IN_CLAIM);
                accumulator.add(new Context(TOWN, townClaim.get().getParent().getUniqueId().toString()));

                SpongyTowns.getManager().getPlotAt(player.getLocation()).ifPresent(plot ->
                        accumulator.add(new Context(PLOT, plot.get().getParent().getUniqueId().toString()))
                );
            } else {
                accumulator.add(NOT_IN_CLAIM);
            }
        }
    }

    @Override
    public boolean matches(Context context, Subject subject) {
        if (!context.equals(IN_CLAIM) && !context.equals(NOT_IN_CLAIM) && !context.getKey().equals(TOWN)) {
            return false;
        }

        final Optional<CommandSource> commandSource = subject.getCommandSource();
        if (!commandSource.isPresent() || !(commandSource.get() instanceof Player)) {
            return false;
        }

        final Player player = (Player) commandSource.get();
        Optional<TownClaim> claim = SpongyTowns.getManager().getClaimAt(player.getLocation());
        if (context.equals(IN_CLAIM) && !claim.isPresent()) {
            return false;
        }

        if (context.equals(NOT_IN_CLAIM) && claim.isPresent()) {
            return false;
        }

        if (context.getKey().equals(TOWN)) {
            /*
            if (!claim.isPresent()) {
                return false;
            }

            if (!context.getValue().equals(String.valueOf(claim.get().getParent().getId()))) {
                return false;
            }*/
        }

        return true;
    }

}
