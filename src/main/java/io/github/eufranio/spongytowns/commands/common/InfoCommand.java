package io.github.eufranio.spongytowns.commands.common;

import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.util.Util;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by Frani on 06/03/2018.
 */
public class InfoCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource sender, CommandContext context) throws CommandException {
        Claim target = context.<Claim>getOne("claim").orElse(null);
        if (target == null) {
            if (!(sender instanceof Player)) {
                Util.error("Specify an claim when running this command from console!");
            }
            target = SpongyTowns.getManager().getTown(((Player) sender).getUniqueId()).orElse(null);
            if (target == null) {
                Util.error("You aren't on a claim! Join one or specify one in the command!");
            }
        }

        target.sendInfo(sender);
        return CommandResult.success();
    }
}
