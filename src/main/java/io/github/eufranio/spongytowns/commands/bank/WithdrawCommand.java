package io.github.eufranio.spongytowns.commands.bank;

import com.google.common.collect.ImmutableMap;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.display.BankMessages;
import io.github.eufranio.spongytowns.display.EconomyMessages;
import io.github.eufranio.spongytowns.display.TownMessages;
import io.github.eufranio.spongytowns.interfaces.Bank;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.util.Util;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;

import java.util.Optional;

/**
 * Created by Frani on 14/03/2018.
 */
public class WithdrawCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource sender, CommandContext context) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException(Text.of("This command can only be used by players!"));
        }
        Player p = (Player) sender;

        int amount = context.<Integer>getOne("amount").get();
        Claim claim = SpongyTowns.getManager().getClaimAt(p.getLocation()).orElse(null);
        Optional<Claim> opt = context.getOne("claim");
        if (claim == null || opt.isPresent()) {
            if (!opt.isPresent()) {
                Util.error(TownMessages.getInstance().UNKNOWN_CLAIM);
            }
            claim = opt.get();
        }

        if (!claim.getOwner().equals(p.getUniqueId())) {
            Util.error(BankMessages.getInstance().ONLY_OWNER_WITHDRAW);
        }

        ResultType result = claim.getBank().withdraw(Bank.player(p.getUniqueId()), amount, EconomyMessages.getInstance().getReasons().WITHDRAW.toText(), p);
        if (result == ResultType.ACCOUNT_NO_FUNDS) {
            Util.error(EconomyMessages.getInstance().NO_FUNDS.apply(ImmutableMap.of(
                    "price", amount
            )).toText());
        } else if (result != ResultType.SUCCESS) {
            Util.error(EconomyMessages.getInstance().ERROR_WITHDRAW.apply(ImmutableMap.of(
                    "reason", result.name()
            )).toText());
        }

        sender.sendMessage(EconomyMessages.getInstance().SUCESSFULL_WITHDRAW.apply(ImmutableMap.of(
                "amount", amount
        )).toText());

        return CommandResult.success();
    }
}
