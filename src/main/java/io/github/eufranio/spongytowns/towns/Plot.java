package io.github.eufranio.spongytowns.towns;

import com.google.common.collect.Lists;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.interfaces.ClaimBlock;
import io.github.eufranio.spongytowns.interfaces.Identifiable;
import io.github.eufranio.spongytowns.util.ReceiverType;
import io.github.eufranio.spongytowns.util.Util;
import lombok.Getter;
import lombok.Setter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.time.Instant;
import java.util.*;

/**
 * Created by Frani on 27/01/2018.
 */
@DatabaseTable(tableName = "plots")
@Getter
@Setter
public class Plot extends BaseDaoEnabled<Plot, UUID> implements Identifiable, Claim {

    @DatabaseField(generatedId = true, index = true)
    public UUID uuid;

    @DatabaseField
    public UUID townId;

    @DatabaseField
    public UUID world;

    @DatabaseField
    public UUID owner;

    @DatabaseField
    public String dateCreated;

    @DatabaseField
    public String lastActive;

    @DatabaseField
    public int spawnX;

    @DatabaseField
    public int spawnY;

    @DatabaseField
    public int spawnZ;

    @DatabaseField
    public UUID spawnWorld;

    @DatabaseField
    public boolean forSale;

    @DatabaseField
    public int price;

    @DatabaseField
    public int plotType;

    @DatabaseField
    public String name;

    @DatabaseField
    public String displayName;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public ArrayList<UUID> members;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public HashMap<String, Object> customData;

    @DatabaseField
    public boolean admin;

    @Override
    public void addMember(UUID uuid) {
        this.members.add(uuid);
        SpongyTowns.getManager().getResidents().get(uuid).add(this);
        this.updateStorage();
    }

    @Override
    public void removeMember(UUID uuid) {
        this.members.remove(uuid);
        SpongyTowns.getManager().getResidents().get(uuid).remove(this);
        this.updateStorage();
    }

    @Override
    public void setDisplayName(Text name) {
        this.displayName = Util.fromText(name);
        this.updateStorage();
    }

    @Override
    public Text getDisplayName() {
        return Util.toText(this.displayName);
    }

    @Override
    public Account getAccount() {
        return SpongyTowns.getEconomyService().getOrCreateAccount(this.getUuid()).get();
    }

    private List<ClaimBlock> blocks = Lists.newArrayList();

    private Town parent;

    @Override
    public Plot get() {
        return this;
    }

    public static Plot of(String name, Town parent, UUID owner, Location<World> location) {
        Plot plot = new Plot();
        plot.setDao(SpongyTowns.getStorage().getPlots());
        plot.setTownId(parent.getUniqueId());
        plot.setParent(parent);
        plot.setWorld(location.getExtent().getUniqueId());
        plot.setOwner(owner);
        plot.setName(name);
        plot.setDisplayName(Text.of(name));
        plot.setDateCreated(Instant.now().toString());
        plot.setLastActive(Instant.now().toString());
        plot.setSpawn(location);
        plot.setForSale(false);
        plot.setPrice(-1);
        plot.setPlotType(0);
        plot.setMembers(new ArrayList<>());
        plot.setCustomData(new HashMap<>());
        plot.setAdmin(false);
        SpongyTowns.getStorage().save(plot);

        return plot;
    }

    Location<World> spawn;

    @Override
    public Location<World> getSpawn() {
        if (this.spawn == null) {
            this.spawn = new Location<World>(Sponge.getServer().getWorld(this.getSpawnWorld()).get(), this.getSpawnX(), this.getSpawnY(), this.getSpawnZ());
        }
        return this.spawn;
    }

    @Override
    public void setSpawn(Location<World> loc) {
        this.spawn = loc;
        this.spawnX = loc.getBlockX();
        this.spawnY = loc.getBlockY();
        this.spawnZ = loc.getBlockZ();
        this.spawnWorld = loc.getExtent().getUniqueId();
    }

    @Override
    public ClaimBlock claim(Location<World> chunk) {
        return PlotClaim.of(this, parent, chunk.getChunkPosition(), chunk.getExtent().getUniqueId());
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public ReceiverType getReceiverType() {
        return ReceiverType.PLOT;
    }

}
