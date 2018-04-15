package io.github.eufranio.spongytowns.commands.town;

import com.google.common.collect.ImmutableMap;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.display.PermissionMessages;
import io.github.eufranio.spongytowns.display.ResidentMessages;
import io.github.eufranio.spongytowns.display.TownMessages;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.permission.Permissions;
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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.time.Instant;

/**
 * Created by Frani on 14/04/2018.
 */
public class InviteCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource sender, CommandContext context) throws CommandException {
        Claim claim = context.<Claim>getOne("claim").orElse(null);
        if (claim == null) {
            if (!(sender instanceof Player)) {
                Util.error("If running this command from console, you must specify an claim!");
            }
            claim = SpongyTowns.getManager().getClaimAt(((Player) sender).getLocation()).orElse(null);
            if (claim == null) {
                Util.error(TownMessages.getInstance().CHUNK_NOT_CLAIMED);
            }
        }

        if (sender instanceof Player &&
                !claim.getOwner().equals(((Player) sender).getUniqueId()) &&
                !sender.hasPermission(Permissions.ADMIN_INVITE)) {
            claim.sendDenyMessage(PermissionMessages.getInstance().ONLY_OWNERS, (Player) sender);
            return CommandResult.success();
        }

        Resident target = context.<Resident>getOne("resident").get();
        InviteSettings i = (InviteSettings) target.get(DataKeys.INVITES);

        InviteSettings.Invite invite = new InviteSettings.Invite();
        invite.setClaim(claim.getUniqueId());
        invite.setSent(Instant.now());
        invite.setSentBy(sender instanceof Player ? ((Player) sender).getUniqueId() : Util.SERVER_UUID);

        i.getInvites().add(invite);
        i.save(target);
        target.updateStorage();

        target.queueMessage(ResidentMessages.getInstance().RECEIVED_INVITE.apply(ImmutableMap.of(
                "claim", claim.getInfoHover(),
                "clickHere", Text.of(TextColors.YELLOW, TextStyles.UNDERLINE, TextStyles.ITALIC, "Click here")
                                    .toBuilder()
                                    .onClick(TextActions.runCommand("/town invites"))
                                    .build()
        )).toText());
        sender.sendMessage(ResidentMessages.getInstance().SENT_INVITE.apply(ImmutableMap.of(
                "resident", target.getUser().getName()
        )).toText());
        return CommandResult.success();
    }

}
