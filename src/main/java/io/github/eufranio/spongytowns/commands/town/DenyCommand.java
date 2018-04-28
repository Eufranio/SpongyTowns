package io.github.eufranio.spongytowns.commands.town;

import com.google.common.collect.ImmutableMap;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.display.ResidentMessages;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.permission.Resident;
import io.github.eufranio.spongytowns.storage.DataKeys;
import io.github.eufranio.spongytowns.storage.InviteSettings;
import io.github.eufranio.spongytowns.util.Util;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.UUID;

/**
 * Created by Frani on 16/04/2018.
 */
public class DenyCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource sender, CommandContext context) throws CommandException {
        Claim claim = context.<Claim>getOne("claim").orElse(null);
        if (claim == null) {
            UUID uuid = context.<UUID>getOne("uuid").orElse(null);
            if (uuid != null) {
                claim = SpongyTowns.getManager().getClaim(uuid);
            }
        }

        Resident res = SpongyTowns.getManager().getResidents().get(((Player) sender).getUniqueId());
        InviteSettings i = (InviteSettings) res.get(DataKeys.INVITES);

        InviteSettings.Invite invite = null;
        if (i.getInvites().size() == 1) {
            invite = i.getInvites().get(0);
            if (claim == null) {
                claim = SpongyTowns.getManager().getClaim(i.getInvites().get(0).getClaim());
                if (claim == null) {
                    Util.error("Invalid claim!");
                }
            }
        } else if (i.getInvites().size() > 1) {
            Util.error(ResidentMessages.getInstance().YOU_HAVE_MANY_INVITES);
        } else {
            Util.error(ResidentMessages.getInstance().YOU_HAVE_NO_INVITES);
        }

        if (!invite.getClaim().equals(claim.getUniqueId())) {
            Util.error("You don't have an invite for that claim!");
        }

        i.getInvites().remove(invite);
        i.save(res);
        res.updateStorage();

        sender.sendMessage(ResidentMessages.getInstance().DENIED_INVITE.apply(ImmutableMap.of(
                "claim", claim.getInfoHover()
        )).toText());

        return CommandResult.success();
    }

}
