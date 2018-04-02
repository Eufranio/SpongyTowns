package io.github.eufranio.spongytowns.towns;

import com.flowpowered.math.vector.Vector3i;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.interfaces.ClaimBlock;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Frani on 27/01/2018.
 */
@DatabaseTable(tableName = "plotClaims")
@Getter
@Setter
public class PlotClaim extends BaseDaoEnabled<PlotClaim, UUID> implements ClaimBlock {

    @DatabaseField(generatedId = true, index = true)
    public UUID uuid;

    @DatabaseField
    public UUID townId;

    @DatabaseField
    public UUID plotId;

    @DatabaseField
    public int chunkX;

    @DatabaseField
    public int chunkZ;

    @DatabaseField
    public UUID world;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public HashMap<String, Object> customData;

    public Plot parent;

    public Town town;

    private Vector3i location;

    @Override
    public Vector3i getLocation() {
        if (this.location == null) {
            this.location = new Vector3i(this.getChunkX(), 0, this.getChunkZ());
        }
        return this.location;
    }

    @Override
    public void setLocation(Vector3i loc, UUID world) {
        this.location = loc;
        this.chunkX = loc.getX();
        this.chunkZ = loc.getZ();
        this.world = world;
    }

    @Override
    public PlotClaim get() {
        return this;
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    public static PlotClaim of(Plot parent, Town town, Vector3i location, UUID world) {
        PlotClaim claim = new PlotClaim();
        claim.setDao(SpongyTowns.getStorage().getPlotClaims());
        claim.setParent(parent);
        claim.setTown(town);
        claim.setTownId(town.getUniqueId());
        claim.setPlotId(parent.getUniqueId());
        claim.setLocation(location, world);
        claim.setCustomData(new HashMap<>());
        SpongyTowns.getStorage().save(claim);
        parent.getBlocks().add(claim);
        return claim;
    }

}