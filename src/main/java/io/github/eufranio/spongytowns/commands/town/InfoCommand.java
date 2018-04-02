package io.github.eufranio.spongytowns.commands.town;

import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.towns.Town;
import io.github.eufranio.spongytowns.util.Util;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;

/**
 * Created by Frani on 06/03/2018.
 */
public class InfoCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource sender, CommandContext context) throws CommandException {
        Town target = context.<Town>getOne("town").orElse(null);
        if (target == null) {
            if (!(sender instanceof Player)) {
                Util.error("Specify a Town when running this command from console!");
            }
            target = SpongyTowns.getManager().getTown(((Player) sender).getUniqueId()).orElse(null);
            if (target == null) {
                Util.error("You aren't on a Town! Join one or specify one in the command!");
            }
        }

        target.sendInfo(sender);
        return CommandResult.success();
    }
}
