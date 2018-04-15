package io.github.eufranio.spongytowns.commands.town;

import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.display.ResidentMessages;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.permission.Resident;
import io.github.eufranio.spongytowns.storage.DataKeys;
import io.github.eufranio.spongytowns.storage.InviteSettings;
import io.github.eufranio.spongytowns.util.InfoBuilder;
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

import java.sql.Date;

/**
 * Created by Frani on 14/04/2018.
 */
public class InvitesCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource sender, CommandContext context) throws CommandException {
        Resident res = context.<Resident>getOne("resident").orElse(null);
        if (res == null) {
            if (!(sender instanceof Player)) {
                Util.error("If you're running this from console, you must specify an resident!");
            }
            res = SpongyTowns.getManager().getResidents().get(((Player) sender).getUniqueId());
        }

        InviteSettings i = (InviteSettings) res.get(DataKeys.INVITES);
        if (i.getInvites().isEmpty()) {
            Util.error(ResidentMessages.getInstance().YOU_HAVE_NO_INVITES);
        }

        InfoBuilder b = InfoBuilder.create();
        i.getInvites().forEach(inv -> {
            Claim claim = SpongyTowns.getManager().getClaim(inv.getClaim());
            b.add(Text.of(
                    TextColors.YELLOW, " * ",
                    TextColors.GRAY, " Sent by ",
                    Text.of(TextColors.GOLD, inv.getSentBy().equals(Util.SERVER_UUID) ? "SERVER" : Util.getUser(inv.getSentBy()).getName())
                            .toBuilder()
                            .onHover(TextActions.showText(Text.of(inv.getSentBy().toString())))
                            .build(),
                    ", ",
                    TextColors.AQUA, Date.from(inv.getSent()).toGMTString(),
                    TextColors.GRAY, " | ",
                    SpongyTowns.getManager().getClaim(inv.getClaim()).getInfoHover(),
                    TextColors.GRAY, " | ",
                    Text.of(TextColors.GREEN, "[ACCEPT]")
                            .toBuilder()
                            .onHover(TextActions.showText(Text.of("Click to accept this invite")))
                            .onClick(TextActions.runCommand("/town accept " + claim.getUniqueId()))
                            .build(),
                    " ",
                    Text.of(TextColors.RED, "[DENY]")
                            .toBuilder()
                            .onHover(TextActions.showText(Text.of("Click to deny this invite")))
                            .onClick(TextActions.runCommand("/town deny " + claim.getUniqueId()))
                            .build()
            ));
        });

        b.sendTo(sender);
        return CommandResult.success();
    }

}
