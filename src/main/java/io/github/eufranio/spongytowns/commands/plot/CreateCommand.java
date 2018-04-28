package io.github.eufranio.spongytowns.commands.plot;

import com.google.common.collect.ImmutableMap;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.display.EconomyMessages;
import io.github.eufranio.spongytowns.display.PermissionMessages;
import io.github.eufranio.spongytowns.display.TownMessages;
import io.github.eufranio.spongytowns.interfaces.ClaimBlock;
import io.github.eufranio.spongytowns.permission.Options;
import io.github.eufranio.spongytowns.towns.Plot;
import io.github.eufranio.spongytowns.towns.Town;
import io.github.eufranio.spongytowns.towns.TownClaim;
import io.github.eufranio.spongytowns.util.Util;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Frani on 16/04/2018.
 */
public class CreateCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource sender, CommandContext context) throws CommandException {
        if (!(sender instanceof Player)) throw new CommandException(Text.of("This command can only be used by players!"));

        UUID uuid = Util.getPlayerOrTarget(sender, context, "create");
        Player player = (Player) sender;
        Location<World> location = player.getLocation();
        Optional<ClaimBlock> claimAt = SpongyTowns.getManager().getClaimBlockAt(location);
        if (claimAt.isPresent() && claimAt.get().getParent() instanceof Plot) {
            Text msg = TownMessages.getInstance().ALREADY_CLAIMED.apply(
                    ImmutableMap.of("town", claimAt.get().getParent().getInfoHover())
            ).toText();
            throw new CommandException(msg);
        }

        Text name = context.<Text>getOne("name").get();
        if (SpongyTowns.getManager().getPlot(name.toPlain()).isPresent()) {
            throw new CommandException(TownMessages.getInstance().NAME_USED.toText());
        }

        Town parent = null;
        Optional<TownClaim> b = SpongyTowns.getManager().getClaimBlockAt(location.getBlockPosition(), location.getExtent().getUniqueId());
        if (b.isPresent()) {
            parent = b.get().getParent();
            if (!parent.getOwner().equals(uuid)) {
                Util.error(PermissionMessages.getInstance().ONLY_OWNERS.apply(ImmutableMap.of(
                        "claim", parent.getInfoHover()
                )).toText());
            }
        } else {
            Util.error(TownMessages.getInstance().CHUNK_NOT_CLAIMED);
        }

        if (SpongyTowns.isEconomyEnabled()) {
            int price = Util.getIntOption(sender, Options.PLOT_CREATION_COST);
            EconomyService s = SpongyTowns.getEconomyService();
            Account account = s.getOrCreateAccount(player.getUniqueId()).get();
            TransactionResult r = account.withdraw(s.getDefaultCurrency(), new BigDecimal(price), Sponge.getCauseStackManager().getCurrentCause());

            if (r.getResult() == ResultType.ACCOUNT_NO_FUNDS) {
                Util.error(EconomyMessages.getInstance().NO_FUNDS.apply(ImmutableMap.of(
                        "price", price
                )).toText());
            } else if (r.getResult() != ResultType.SUCCESS) {
                Util.error(EconomyMessages.getInstance().ERROR_WITHDRAW.apply(ImmutableMap.of(
                        "reason", r.getResult().name()
                )).toText());
            }
        }

        Plot plot = SpongyTowns.getManager().createPlot(name.toPlain(), parent, location, uuid);
        plot.setDisplayName(name);
        sender.sendMessage(
                TownMessages.getInstance().CREATE.apply(
                        ImmutableMap.of("town", plot.getInfoHover())
                ).build()
        );
        return CommandResult.success();
    }

}
