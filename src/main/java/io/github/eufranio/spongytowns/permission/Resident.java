package io.github.eufranio.spongytowns.permission;

import com.google.common.collect.Lists;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.interfaces.Identifiable;
import io.github.eufranio.spongytowns.interfaces.Persistant;
import io.github.eufranio.spongytowns.interfaces.Purgeable;
import io.github.eufranio.spongytowns.towns.Plot;
import io.github.eufranio.spongytowns.towns.Town;
import io.github.eufranio.spongytowns.util.Util;
import lombok.Getter;
import lombok.Setter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Frani on 24/02/2018.
 */
@DatabaseTable(tableName = "residents")
@Getter
@Setter
public class Resident extends BaseDaoEnabled<Resident, UUID> implements Persistant, Purgeable {

    @DatabaseField(index = true, id = true)
    public UUID id;

    // Town <-> TownRank
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public ArrayList<UUID> towns;

    // Plot <-> TownRank
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public ArrayList<UUID> plots;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public HashMap<String, Object> customData;

    @DatabaseField
    public String lastActive;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public ArrayList<String> pendingMessages = Lists.newArrayList();

    @Override
    public Resident get() {
        return this;
    }

    public void add(Claim claim) {
        if (claim instanceof Town) {
            this.towns.add(claim.getUniqueId());
        } else if (claim instanceof Plot) {
            this.plots.add(claim.getUniqueId());
        }
        this.updateStorage();
    }

    public void remove(Claim claim) {
        if (claim instanceof Town) {
            this.towns.remove(claim.getUniqueId());
        } else if (claim instanceof Plot) {
            this.plots.remove(claim.getUniqueId());
        }
        this.updateStorage();
    }

    @Override
    public UUID getUniqueId() {
        return this.id;
    }

    @Override
    public String getDateCreated() {
        return null;
    }

    public void updateLastActive() {
        this.lastActive = Instant.now().toString();
        this.updateStorage();
    }

    public void queueMessage(Text message) {
        Player player = Sponge.getServer().getPlayer(this.id).orElse(null);
        if (player != null) {
            player.sendMessage(message);
        } else {
            this.pendingMessages.add(Util.fromText(message));
        }
    }

    public User getUser() {
        return Util.getUser(this.id);
    }

    public Claim getTown() {
        return SpongyTowns.getManager().getTown(this.id).orElse(null);
    }

}