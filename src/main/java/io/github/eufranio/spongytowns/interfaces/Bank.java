package io.github.eufranio.spongytowns.interfaces;

import com.google.common.collect.ImmutableMap;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.display.BankMessages;
import io.github.eufranio.spongytowns.display.EconomyMessages;
import io.github.eufranio.spongytowns.display.TownMessages;
import io.github.eufranio.spongytowns.managers.TownManager;
import io.github.eufranio.spongytowns.storage.DataKeys;
import io.github.eufranio.spongytowns.storage.TaxSettings;
import io.github.eufranio.spongytowns.storage.Transaction;
import io.github.eufranio.spongytowns.util.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Frani on 08/03/2018.
 */
public interface Bank extends Identifiable {

    Account getAccount();

    ReceiverType getReceiverType();

    // player = who caused the transference, via command for example
    default ResultType transferTo(Bank receiver, int amount, Text reason, Player player) {
        Transaction transaction = Transaction.of(this, receiver, reason, TransactionType.TRANSFER, amount, player);
        ResultType r = transaction.process(this, receiver).getResult();
        transaction.setResult(r.name());
        transaction.updateStorage();
        return r;
    }

    default ResultType withdraw(Bank receiver, int amount, Text reason, Player player) {
        Transaction transaction = Transaction.of(this, receiver, reason, TransactionType.WITHDRAW, amount, player);
        ResultType r = transaction.process(this, null).getResult(); // null since we're sending the money to te server
        transaction.setResult(r.name());
        transaction.updateStorage();
        return r;
    }

    default ResultType deposit(Player player, int amount, Text reason) {
        Bank from = Bank.player(player.getUniqueId());
        Transaction transaction = Transaction.of(from, this, reason, TransactionType.DEPOSIT, amount, player);
        ResultType r = transaction.process(from, this).getResult();
        transaction.setResult(r.name());
        transaction.updateStorage();
        return r;
    }

    default void resetAccount() {
        this.getAccount().resetBalances(Sponge.getCauseStackManager().getCurrentCause());
    }

    static Bank player(UUID uuid) {
        return new Bank() {
            @Override
            public Account getAccount() {
                return SpongyTowns.getEconomyService().getOrCreateAccount(uuid).get();
            }

            @Override
            public ReceiverType getReceiverType() {
                return ReceiverType.PLAYER;
            }

            @Override
            public UUID getUniqueId() {
                return uuid;
            }
        };
    }

    static Bank server() {
        return new Bank() {
            @Override
            public Account getAccount() {
                return null;
            }

            @Override
            public ReceiverType getReceiverType() {
                return ReceiverType.SERVER;
            }

            @Override
            public UUID getUniqueId() {
                return Util.SERVER_UUID;
            }
        };
    }

    default Text getBankInfo() {
        return new InfoBuilder()
                .add(BankMessages.getInstance().getInfo().BALANCE.apply(ImmutableMap.of(
                        "balance", "$" + this.getAccount().getBalance(SpongyTowns.getEconomyService().getDefaultCurrency())
                )).toText())
                .add(BankMessages.getInstance().getInfo().TAX.apply(ImmutableMap.of(
                        "tax", "$" + (this instanceof Claim ? ((Claim) this).getTax() : "~")
                )).toText())
                .add(BankMessages.getInstance().getInfo().DUE_DAYS.apply(ImmutableMap.of(
                        "days", this instanceof Claim ? ((TaxSettings)((Claim) this).get(DataKeys.TAX)).getDueDays() : "~"
                )).toText())
                .add(BankMessages.getInstance().getInfo().FROZEN.apply(ImmutableMap.of(
                        "frozen", this instanceof Claim ? ((TaxSettings)((Claim) this).get(DataKeys.TAX)).isFrozen() : "~"
                )).toText())
                .add(BankMessages.getInstance().getInfo().TRANSACTIONS.apply(ImmutableMap.of(
                        "transactions", Text.builder()
                                .append(Text.of("[CLICK HERE]"))
                                .onClick(TextActions.executeCallback(this::sendTransactions))
                                .build()
                )).toText())
                .build();
    }

    default void sendBankInfo(MessageReceiver receiver) {
        PaginationList.builder()
                .padding(Text.of(
                        TextStyles.RESET, TextColors.DARK_GRAY, TextStyles.STRIKETHROUGH, "-"
                ))
                .title(Text.of(TextColors.GREEN, "Bank Info"))
                .contents(this.getBankInfo())
                .sendTo(receiver);
    }

    default void sendTransactions(MessageReceiver receiver) {
        List<Text> t = SpongyTowns.getManager().getTransactions().stream()
                .filter(tr -> tr.getFrom().equals(this.getUniqueId()) || tr.getTo().equals(this.getUniqueId()))
                .map(tr -> Text.of(
                        TextColors.GOLD, " ", tr.getSenderName(), " (" + tr.getSenderType().name() + ")",
                        TextColors.GRAY, " -> ",
                        TextColors.GOLD, tr.getReceiverName(), " (" + tr.getReceiverType().name() + ")",
                        TextColors.GRAY, " | ",
                        tr.getResult().equalsIgnoreCase("SUCCESS") ? TextColors.DARK_GREEN : TextColors.RED, "$" + tr.getAmount())
                        .toBuilder()
                        .onHover(TextActions.showText(new InfoBuilder()
                                .add(Text.of(
                                        TextColors.GREEN, " Transaction Info",
                                        Text.NEW_LINE,
                                        TextColors.GRAY, TextStyles.STRIKETHROUGH, "--------------------------"
                                ))
                                .add(Text.of(
                                        TextColors.YELLOW, "Date: ",
                                        TextColors.AQUA, tr.getTime().toGMTString()
                                ))
                                .add(Text.of(
                                        TextColors.YELLOW, "Type: ",
                                        TextColors.BLUE, tr.getType().name()
                                ))
                                .add(Text.of(
                                        TextColors.YELLOW, "Reason: ",
                                        TextColors.WHITE, Util.toText(tr.getReason())
                                ))
                                .add(Text.of(
                                        TextColors.YELLOW, "Result: ",
                                        tr.getResult().equalsIgnoreCase("SUCCESS") ? TextColors.GREEN : TextColors.RED, tr.getResult()
                                ))
                                .add(Text.of(
                                        TextColors.YELLOW, "Player: ",
                                        TextColors.LIGHT_PURPLE, tr.getUsername(),
                                        Text.NEW_LINE
                                ))
                                .build()))
                        .build()
                )
                .collect(Collectors.toList());

        PaginationList.builder()
                .contents(t)
                .padding(Text.of(
                        TextStyles.RESET, TextColors.DARK_GRAY, TextStyles.STRIKETHROUGH, "-"
                ))
                .title(Text.of(TextColors.GREEN, "Transaction List"))
                .sendTo(receiver);
    }

}
