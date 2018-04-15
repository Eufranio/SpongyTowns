package io.github.eufranio.spongytowns.commands.town;

import com.google.common.collect.ImmutableMap;
import io.github.eufranio.spongytowns.SpongyTowns;
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

import java.util.UUID;

/**
 * Created by Frani on 14/04/2018.
 */
public class SetOwnerCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource sender, CommandContext context) throws CommandException {
        UUID user = context.<UUID>getOne("uuid").get();
        Claim claim = context.<Claim>getOne("claim").orElse(null);
        if (claim == null) {
            claim = SpongyTowns.getManager()
                    .getClaimAt(((Player) sender).getLocation())
                    .orElse(null);
        }
        if (claim == null) {
            Util.error(TownMessages.getInstance().CHUNK_NOT_CLAIMED);
        }
        Resident res = SpongyTowns.getManager().getResidents().get(claim.getOwner());
        if (res != null) {
            res.getTowns().remove(claim.getUniqueId());
            res.updateStorage();
        }
        claim.setOwner(user);
        sender.sendMessage(TownMessages.getInstance().CHANGED_OWNER.apply(ImmutableMap.of(
                "claim", claim.getInfoHover()
        )).toText());
        claim.updateStorage();
        return CommandResult.success();
    }

}
