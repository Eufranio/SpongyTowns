package io.github.eufranio.spongytowns.storage;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.interfaces.Bank;
import io.github.eufranio.spongytowns.interfaces.Persistant;
import io.github.eufranio.spongytowns.util.ReceiverType;
import io.github.eufranio.spongytowns.util.TransactionType;
import lombok.Getter;
import lombok.Setter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Frani on 13/03/2018.
 */
@DatabaseTable(tableName = "transactions")
@Getter
@Setter
public class Transaction extends BaseDaoEnabled<Transaction, UUID> implements Persistant {

    @DatabaseField(generatedId = true)
    public UUID uuid;

    @DatabaseField
    public UUID from;

    @DatabaseField
    public UUID to;

    @DatabaseField
    public String username;

    @DatabaseField
    public String reason;

    @DatabaseField
    public Date time;

    @DatabaseField
    public TransactionType type;

    @DatabaseField
    public ReceiverType receiverType;

    @DatabaseField
    public ReceiverType senderType;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public HashMap<String, Object> customData;

    @DatabaseField
    public int amount;

    @Override
    public Transaction get() {
        return this;
    }

    public static Transaction of(Bank from, Bank to, String reason, TransactionType type, int amount, Player player) {
        Transaction t = new Transaction();
        t.setFrom(from.getUniqueId());
        t.setTo(to.getUniqueId());
        t.setReason(reason);
        t.setType(type);
        t.setTime(new Date());
        t.setAmount(amount);
        t.setReceiverType(to.getReceiverType());
        t.setSenderType(from.getReceiverType());
        if (player != null) t.setUsername(player.getName());
        t.setCustomData(new HashMap<>());
        t.setDao(SpongyTowns.getStorage().getTransactions());
        SpongyTowns.getStorage().saveTransaction(t);
        return t;
    }

    public TransactionResult process(Bank from, Bank to) {
        Cause cause = Sponge.getCauseStackManager().getCurrentCause();
        BigDecimal amount = new BigDecimal(this.getAmount());
        Currency currency = SpongyTowns.getEconomyService().getDefaultCurrency();
        if (this.getReceiverType() == ReceiverType.SERVER) {
            return from.getAccount().withdraw(currency, amount, cause);
        } else {
            return from.getAccount().transfer(to.getAccount(), currency, amount, cause);
        }
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

}
