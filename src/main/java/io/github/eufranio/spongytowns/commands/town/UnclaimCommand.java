package io.github.eufranio.spongytowns.commands.town;

import com.google.common.collect.ImmutableMap;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.display.PermissionMessages;
import io.github.eufranio.spongytowns.display.TownMessages;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.interfaces.ClaimBlock;
import io.github.eufranio.spongytowns.permission.Permissions;
import io.github.eufranio.spongytowns.towns.Plot;
import io.github.eufranio.spongytowns.towns.PlotClaim;
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
        Optional<ClaimBlock> opt = SpongyTowns.getManager().getClaimBlockAt(location);
        if (!opt.isPresent()) {
            Util.error(TownMessages.getInstance().CHUNK_NOT_CLAIMED.toText());
        }

        ClaimBlock b = opt.get();
        if (b instanceof PlotClaim) {
            b = b.getParent().getParent().getBlockAt(location).get();
        }

        if (!b.getParent().getOwner().equals(player.getUniqueId()) && !player.hasPermission(Permissions.UNCLAIM_ADMIN)) {
            Util.error(PermissionMessages.getInstance().NO_PERMISSION_CLAIM.apply(ImmutableMap.of("town", b.getParent().getInfoHover())).toText());
        }

        if (b.getParent().getBlocks().size() == 1) {
            final ClaimBlock block = b;
            sender.sendMessage(TownMessages.getInstance().UNCLAIM_LAST_CHUNK.apply(ImmutableMap.of(
                    "town", b.getParent().getInfoHover(),
                    "button", Text.of(TextColors.RED, "[CONTINUE]").toBuilder()
                            .onHover(TextActions.showText(Text.of("Click to unclaim anyway")))
                            .onClick(TextActions.executeCallback(src -> {
                                block.getParent().remove();
                                sender.sendMessage(TownMessages.getInstance().UNCLAIM.toText());
                            }))
                            .build()
            )).toText());
            return CommandResult.success();
        }

        b.remove();
        sender.sendMessage(TownMessages.getInstance().UNCLAIM.toText());
        return CommandResult.success();
    }

}
