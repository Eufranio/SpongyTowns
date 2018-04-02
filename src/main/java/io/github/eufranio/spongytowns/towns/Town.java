package io.github.eufranio.spongytowns.towns;

import com.google.common.collect.Lists;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.interfaces.ClaimBlock;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Frani on 19/02/2018.
 */
@DatabaseTable(tableName = "towns")
@Getter
@Setter
public class Town extends BaseDaoEnabled<Town, UUID> implements Claim {

    @DatabaseField(generatedId = true, index = true)
    public UUID uuid;

    @DatabaseField
    public UUID owner;

    @DatabaseField
    public UUID world;

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
    public String team;

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

    private List<Claim> childs = Lists.newArrayList();

    private Location<World> spawn;

    public Location<World> getSpawn() {
        if (this.spawn == null) {
            this.spawn = new Location<World>(Sponge.getServer().getWorld(this.getSpawnWorld()).get(), this.getSpawnX(), this.getSpawnY(), this.getSpawnZ());
        }
        return this.spawn;
    }

    public void setSpawn(Location<World> loc) {
        this.spawn = loc;
        this.spawnX = loc.getBlockX();
        this.spawnY = loc.getBlockY();
        this.spawnZ = loc.getBlockZ();
        this.spawnWorld = loc.getExtent().getUniqueId();
    }

    public static Town of(String name, String team, Location<World> location, UUID owner) {
        Town town = new Town();
        town.setDao(SpongyTowns.getStorage().getTowns());
        town.setOwner(owner);
        town.setTeam(team);
        town.setDisplayName(Text.of(team));
        town.setName(name);
        town.setSpawn(location);
        town.setDateCreated(Instant.now().toString());
        town.setLastActive(Instant.now().toString());
        town.setWorld(location.getExtent().getUniqueId());
        town.setUuid(UUID.randomUUID());
        town.setMembers(new ArrayList<>());
        town.setCustomData(new HashMap<>());
        town.setAdmin(false);
        SpongyTowns.getStorage().save(town);
        return town;
    }

    @Override
    public Town get() {
        return this;
    }

    @Override
    public ClaimBlock claim(Location<World> chunk) {
        return TownClaim.of(this, chunk.getChunkPosition(), chunk.getExtent().getUniqueId());
    }

    @Override
    public ReceiverType getReceiverType() {
        return ReceiverType.TOWN;
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

}
