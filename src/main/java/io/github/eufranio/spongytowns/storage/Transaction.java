package io.github.eufranio.spongytowns.storage;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.interfaces.Bank;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.interfaces.Persistant;
import io.github.eufranio.spongytowns.towns.Town;
import io.github.eufranio.spongytowns.util.ReceiverType;
import io.github.eufranio.spongytowns.util.TransactionType;
import io.github.eufranio.spongytowns.util.Util;
import lombok.Getter;
import lombok.Setter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

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
    public String result;

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

    public static Transaction of(Bank from, Bank to, Text reason, TransactionType type, int amount, Player player) {
        Transaction t = new Transaction();
        t.setFrom(from.getUniqueId());
        t.setTo(to.getUniqueId());
        t.setReason(Util.fromText(reason));
        t.setType(type);
        t.setTime(new Date());
        t.setAmount(amount);
        t.setReceiverType(to.getReceiverType());
        t.setSenderType(from.getReceiverType());
        t.setUsername(player != null ? player.getName() : "~");
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

    public Text getReceiverName() {
        if (this.receiverType == ReceiverType.SERVER) {
            return Text.of("SERVER");
        } else if (this.receiverType == ReceiverType.PLAYER) {
            return Text.of(Util.getUser(this.to).getName());
        } else if (this.receiverType == ReceiverType.TOWN) {
            Claim town = SpongyTowns.getManager().getTowns().get(this.to);
            return Text.of(town != null ? town.getInfoHover() : "Unknown Town");
        } else if (this.receiverType == ReceiverType.PLOT) {
            Claim plot = SpongyTowns.getManager().getPlots().get(this.to);
            return Text.of(plot != null ? plot.getInfoHover() : "Unknown Plot");
        } else {
            return Text.of("Unknown");
        }
    }

    public Text getSenderName() {
        if (this.senderType == ReceiverType.SERVER) {
            return Text.of("SERVER");
        } else if (this.senderType == ReceiverType.PLAYER) {
            return Text.of(Util.getUser(this.from).getName());
        } else if (this.senderType == ReceiverType.TOWN) {
            Claim town = SpongyTowns.getManager().getTowns().get(this.from);
            return Text.of(town != null ? town.getInfoHover().toBuilder().color(TextColors.BLUE) : "Unknown Town");
        } else if (this.senderType == ReceiverType.PLOT) {
            Claim plot = SpongyTowns.getManager().getPlots().get(this.from);
            return Text.of(plot != null ? plot.getInfoHover().toBuilder().color(TextColors.RED) : "Unknown Plot");
        } else {
            return Text.of("Unknown");
        }
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

}
