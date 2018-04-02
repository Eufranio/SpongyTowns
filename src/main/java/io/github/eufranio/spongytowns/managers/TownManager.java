package io.github.eufranio.spongytowns.managers;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Singleton;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.interfaces.ClaimBlock;
import io.github.eufranio.spongytowns.permission.Resident;
import io.github.eufranio.spongytowns.storage.Transaction;
import io.github.eufranio.spongytowns.towns.Plot;
import io.github.eufranio.spongytowns.towns.PlotClaim;
import io.github.eufranio.spongytowns.towns.Town;
import io.github.eufranio.spongytowns.towns.TownClaim;
import lombok.Getter;
import lombok.NonNull;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.xml.soap.Text;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Frani on 27/01/2018.
 */
@Getter
@Singleton
public class TownManager {

    @NonNull
    private Map<UUID, Claim> towns = Maps.newHashMap();

    @NonNull
    private Map<UUID, TownClaim> townClaims = Maps.newHashMap();

    @NonNull
    private Map<UUID, Claim> plots = Maps.newHashMap();

    @NonNull
    private Map<UUID, PlotClaim> plotClaims = Maps.newHashMap();

    @NonNull
    private Map<UUID, Resident> residents = Maps.newHashMap();

    @NonNull
    private List<Transaction> transactions = Lists.newArrayList();

    public Optional<TownClaim> getClaimAt(Location<World> location) {
        return townClaims.values().stream().filter(tc -> tc.getLocation().equals(location.getChunkPosition())).findFirst();
    }

    public Optional<TownClaim> getClaimAt(Vector3i chunk, UUID world) {
        return townClaims.values().stream().filter(tc -> tc.getLocation().equals(chunk) && tc.getWorld().equals(world)).findFirst();
    }

    public Optional<PlotClaim> getPlotAt(Location<World> location) {
        return plotClaims.values().stream().filter(pc -> pc.getLocation().equals(location.getChunkPosition())).findFirst();
    }

    public List<Claim> getClaims() {
        List<Claim> claims = Lists.newArrayList(this.getTowns().values());
        claims.addAll(this.getPlots().values());
        return claims;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getClaimById(Class<T> clazz, int id) {
        Map map;
        if (clazz == Town.class) {
            map = this.towns;
        } else if (clazz == TownClaim.class) {
            map = this.townClaims;
        } else if (clazz == Plot.class) {
            map = this.plots;
        } else {
            map = this.plotClaims;
        }
        return Optional.ofNullable((T) map.get(id));
    }

    public Town createTown(String name, String team, Location<World> location, UUID owner) {
        Town t = Town.of(name, team, location, owner);
        t.claim(location);
        return t;
    }

    public Optional<Claim> getTown(String name) {
        return this.towns.values().stream().filter(t -> t.getName().equalsIgnoreCase(name)).findFirst();
    }

    public Optional<Town> getTown(UUID owner) {
        return this.towns.values().stream().filter(t -> t.getOwner().equals(owner)).map(Town.class::cast).findFirst();
    }

}
