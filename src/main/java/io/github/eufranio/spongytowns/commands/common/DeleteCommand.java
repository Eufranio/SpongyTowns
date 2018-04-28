package io.github.eufranio.spongytowns.commands.common;

import com.google.common.collect.ImmutableMap;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.display.TownMessages;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.permission.Permissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by Frani on 24/02/2018.
 */
public class DeleteCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource sender, CommandContext context) throws CommandException {
        Claim claim = context.<Claim>getOne("claim").get();
        if ((sender instanceof Player && claim.getOwner().equals(((Player) sender).getUniqueId())) ||
                sender.hasPermission(Permissions.DELETE_TOWN_OTHERS)) {
            sender.sendMessage(
                    TownMessages.getInstance().ABOUT_TO_DELETE.apply(
                            ImmutableMap.of("town", claim.getInfoHover(),
                                    "button", Text.of(
                                    TextColors.DARK_RED, "[DELETE ANYWAY]")
                                    .toBuilder()
                                    .onHover(TextActions.showText(
                                            Text.of("Click to delete this claim anyway")
                                    ))
                                    .onClick(TextActions.executeCallback(src -> {
                                        if (SpongyTowns.getManager().getClaim(claim.getUniqueId()) != null) {
                                            src.sendMessage(TownMessages.getInstance().DELETE.apply(ImmutableMap.of("claim", claim.getInfoHover())).toText());
                                            claim.remove();
                                        }
                                    })))
                    ).toText()
            );
        } else {
            throw new CommandException(Text.of("You don't have permission to delete this town!"));
        }
        return CommandResult.success();
    }

}
