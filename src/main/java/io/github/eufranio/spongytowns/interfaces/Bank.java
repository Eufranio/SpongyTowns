package io.github.eufranio.spongytowns.interfaces;

import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.storage.Transaction;
import io.github.eufranio.spongytowns.util.InfoBuilder;
import io.github.eufranio.spongytowns.util.ReceiverType;
import io.github.eufranio.spongytowns.util.TransactionType;
import io.github.eufranio.spongytowns.util.Util;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;

import java.util.UUID;

/**
 * Created by Frani on 08/03/2018.
 */
public interface Bank extends Identifiable {

    Account getAccount();

    ReceiverType getReceiverType();

    // player = who caused the transference, via command for example
    default ResultType transferTo(Player player, Bank receiver, int amount, String reason) {
        Transaction transaction = Transaction.of(this, receiver, reason, TransactionType.TRANSFER, amount, player);
        return transaction.process(this, receiver).getResult();
    }

    default ResultType withdraw(int amount, String reason) {
        Transaction transaction = Transaction.of(this, Bank.server(), reason, TransactionType.WITHDRAW, amount, null);
        return transaction.process(this, null).getResult(); // null since we're sending the money to te server
    }

    default ResultType deposit(Player player, int amount, String reason) {
        Bank from = Bank.player(player.getUniqueId());
        Transaction transaction = Transaction.of(from, this, reason, TransactionType.DEPOSIT, amount, player);
        return transaction.process(from, this).getResult();
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
                .add()
    }

    default void sendBankInfo()


}
