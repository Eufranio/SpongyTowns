package io.github.eufranio.spongytowns.commands.bank;

import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.permission.ClaimContextCalculator;
import io.github.eufranio.spongytowns.util.Util;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.Context;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Frani on 14/03/2018.
 */
public class InfoCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource sender, CommandContext context) throws CommandException {
        if (!(sender instanceof Player)) {
            Util.error("Only playrs can run this command!");
        }

        String type = context.<String>getOne("town or plot").get();
        Map<UUID, Claim> map = type.equalsIgnoreCase("town") ?
                SpongyTowns.getManager().getTowns() :
                type.equalsIgnoreCase("plot") ?
                        SpongyTowns.getManager().getPlots() :
                        null;
        if (map == null) Util.error("Specify \"town\" or \"plot\" in the command!");

        if (!sender.getActiveContexts().contains(ClaimContextCalculator.IN_CLAIM)) {
            Util.error("You're not on a claim!");
        }

        String contextKey;
        if (type.equalsIgnoreCase("town")) {
            contextKey = ClaimContextCalculator.TOWN;
        } else {
            contextKey = ClaimContextCalculator.PLOT;
        }

        String uuid = sender.getActiveContexts().stream()
                .filter(c -> c.getKey().equalsIgnoreCase(contextKey))
                .map(Context::getValue)
                .findFirst()
                .orElse(null);
        if (uuid == null) Util.error("Couldn't get the UUID of your claim!");

        Claim claim = map.get(UUID.fromString(uuid));
        claim.getBank().getBankInfo()

        return CommandResult.success();
    }

}
