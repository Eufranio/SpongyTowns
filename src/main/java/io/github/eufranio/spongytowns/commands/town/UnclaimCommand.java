package io.github.eufranio.spongytowns.commands.town;

import com.google.common.collect.ImmutableMap;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.display.PermissionMessages;
import io.github.eufranio.spongytowns.display.TownMessages;
import io.github.eufranio.spongytowns.permission.Permissions;
import io.github.eufranio.spongytowns.towns.Town;
import io.github.eufranio.spongytowns.towns.TownClaim;
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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * Created by Frani on 06/03/2018.
 */
public class UnclaimCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource sender, CommandContext context) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException(Text.of("This command can only be used by players!"));
        }

        Player player = (Player) sender;
        Location<World> location = player.getLocation();
        Optional<TownClaim> claim = SpongyTowns.getManager().getClaimAt(location);
        if (!claim.isPresent()) {
            Util.error(TownMessages.getInstance().CHUNK_NOT_CLAIMED.toText());
        }

        if (!claim.get().getParent().getOwner().equals(player.getUniqueId()) && !player.hasPermission(Permissions.UNCLAIM_ADMIN)) {
            Util.error(PermissionMessages.getInstance().NO_PERMISSION_CLAIM.apply(ImmutableMap.of("town", claim.get().getParent().getInfoHover())).toText());
        }

        if (claim.get().getParent().getBlocks().size() == 1) {
            sender.sendMessage(TownMessages.getInstance().UNCLAIM_LAST_CHUNK.apply(ImmutableMap.of(
                    "town", claim.get().getParent().getInfoHover(),
                    "button", Text.of(TextColors.RED, "[CONTINUE]").toBuilder()
                            .onHover(TextActions.showText(Text.of("Click to unclaim anyway")))
                            .onClick(TextActions.executeCallback(src -> {
                                claim.get().getParent().remove();
                                sender.sendMessage(TownMessages.getInstance().UNCLAIM.toText());
                            }))
                            .build()
            )).toText());
            return CommandResult.success();
        }

        claim.get().remove();
        sender.sendMessage(TownMessages.getInstance().UNCLAIM.toText());
        return CommandResult.success();
    }

}
