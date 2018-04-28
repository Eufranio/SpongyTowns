package io.github.eufranio.spongytowns.commands.town;

import com.google.common.collect.ImmutableMap;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.display.PermissionMessages;
import io.github.eufranio.spongytowns.display.ResidentMessages;
import io.github.eufranio.spongytowns.display.TownMessages;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.permission.Resident;
import io.github.eufranio.spongytowns.util.Util;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by Frani on 16/04/2018.
 */
public class KickCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource sender, CommandContext context) throws CommandException {
        Claim claim = context.<Claim>getOne("claim").orElse(null);
        if (claim == null) {
            if (!(sender instanceof Player)) {
                Util.error("If you're running this command from console, you must specify an claim!");
            }
            claim = SpongyTowns.getManager().getClaimAt(((Player) sender).getLocation()).orElse(null);
            if (claim == null) {
                Util.error(TownMessages.getInstance().CHUNK_NOT_CLAIMED);
            }
        }

        if (sender instanceof Player && !claim.getOwner().equals(((Player) sender).getUniqueId())) {
            Util.error(PermissionMessages.getInstance().ONLY_OWNERS.apply(ImmutableMap.of(
                    "claim", claim.getInfoHover()
            )).toText());
        }

        Resident res = context.<Resident>getOne("resident").get();
        if (!claim.getMembers().contains(res.getUniqueId())) {
            Util.error(ResidentMessages.getInstance().NOT_PART_OF_CLAIM.apply(ImmutableMap.of(
                    "resident", res.getUser().getName(),
                    "claim", claim.getInfoHover()
            )).toText());
        }

        claim.removeMember(res.getUniqueId());
        sender.sendMessage(ResidentMessages.getInstance().KICKED.apply(ImmutableMap.of(
                "resident", res.getUser().getName(),
                "claim", claim.getInfoHover()
        )).toText());

        return CommandResult.success();
    }

}
