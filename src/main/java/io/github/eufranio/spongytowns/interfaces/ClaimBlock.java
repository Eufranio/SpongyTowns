package io.github.eufranio.spongytowns.interfaces;

import com.flowpowered.math.vector.Vector3i;
import io.github.eufranio.spongytowns.SpongyTowns;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Chunk;

import java.util.UUID;

/**
 * Created by Frani on 20/02/2018.
 */
public interface ClaimBlock extends Persistant {

    Claim getParent();

    Vector3i getLocation();

    UUID getWorld();

    void setLocation(Vector3i location, UUID world);

    default void remove() {
        SpongyTowns.getInstance().getStorage().remove(this);
    }

    default Chunk getChunk() {
        return Sponge.getServer().getWorld(this.getWorld()).get().loadChunk(this.getLocation(), true).get();
    }

}
