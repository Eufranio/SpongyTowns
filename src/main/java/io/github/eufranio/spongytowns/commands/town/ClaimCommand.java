package io.github.eufranio.spongytowns.commands.town;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.display.EconomyMessages;
import io.github.eufranio.spongytowns.display.PermissionMessages;
import io.github.eufranio.spongytowns.display.TownMessages;
import io.github.eufranio.spongytowns.interfaces.Bank;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.interfaces.ClaimBlock;
import io.github.eufranio.spongytowns.permission.Options;
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
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Frani on 26/02/2018.
 */
public class ClaimCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource sender, CommandContext context) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException(Text.of("This command can only be used by players!"));
        }

        Player player = (Player) sender;
        Location<World> location = player.getLocation();
        Optional<ClaimBlock> claim = SpongyTowns.getManager().getClaimBlockAt(location);
        if (claim.isPresent()) {
            Util.error(TownMessages.getInstance().ALREADY_CLAIMED.apply(ImmutableMap.of("town", claim.get().getParent().getInfoHover())).toText());
        }

        boolean outpost = false;
        Town parent = context.<Town>getOne("town").orElse(null);
        if (parent == null) { // if no town was supplied in the command, handle nearest
            TownClaim closestClaim = Util.getClosestClaim(location).orElse(null);
            if (closestClaim == null) {
                Util.error(TownMessages.getInstance().OUTPOST_NO_NAME.toText());
            }
            if (!player.hasPermission(Permissions.CLAIM_OUTPOST)) {
                Util.error(PermissionMessages.getInstance().NO_PERMISSION_OUTPOST.toText());
            }
            parent = closestClaim.getParent();
            outpost = true;
        }

        if (!parent.getOwner().equals(player.getUniqueId()) && !player.hasPermission(Permissions.CLAIM_ADMIN)) {
            Util.error(PermissionMessages.getInstance().NO_PERMISSION_CLAIM.apply(ImmutableMap.of("town", parent.getInfoHover())).toText());
        }

        List<ClaimBlock> nearBlocks = Util.getNearBlocks(player, player.getLocation(), parent);
        if (!nearBlocks.isEmpty()) {
            List<Claim> nearClaims = Lists.newArrayList();
            nearBlocks.forEach(block -> {
                if (!nearClaims.contains(block.getParent())) nearClaims.add(block.getParent());
            });
            Util.error(TownMessages.getInstance().TOWNS_NEAR.apply(ImmutableMap.of(
                    "count", nearClaims.size(),
                    "chunks", Util.getIntOption(player, Options.CHUNKS_BETWEEN_TOWNS),
                    "towns", Text.joinWith(Text.of(TextColors.YELLOW, ", "), nearClaims.stream().map(Claim::getInfoHover).collect(Collectors.toList()))
            )).toText());
        }

        if (SpongyTowns.isEconomyEnabled()) {
            int price = Util.getIntOption(sender, outpost ? Options.CLAIM_COST_OUTPOST : Options.CLAIM_COST);
            ResultType r = parent.getBank().withdraw(Bank.server(), price, EconomyMessages.getInstance().getReasons().CLAIM.toText(), player);

            if (r == ResultType.ACCOUNT_NO_FUNDS) {
                Util.error(TownMessages.getInstance().NO_FUNDS_CLAIM.apply(ImmutableMap.of(
                        "town", parent.getName(),
                        "price", price
                )).toText());
            } else if (r != ResultType.SUCCESS) {
                Util.error(EconomyMessages.getInstance().COULD_NOT_WITHDRAW_TOWN.apply(ImmutableMap.of(
                        "reason", r.name()
                )).toText());
            }
        }

        parent.claim(location);
        sender.sendMessage(TownMessages.getInstance().CLAIM.apply(ImmutableMap.of(
                "town", parent.getInfoHover()
        )).toText());
        return CommandResult.success();
    }
}
