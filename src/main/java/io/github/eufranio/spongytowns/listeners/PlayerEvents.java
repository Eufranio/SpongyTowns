package io.github.eufranio.spongytowns.listeners;

import com.flowpowered.math.vector.Vector3i;
import com.google.inject.Singleton;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.display.TownMessages;
import io.github.eufranio.spongytowns.display.Visual;
import io.github.eufranio.spongytowns.permission.Options;
import io.github.eufranio.spongytowns.permission.Permissions;
import io.github.eufranio.spongytowns.permission.Resident;
import io.github.eufranio.spongytowns.towns.TownClaim;
import io.github.eufranio.spongytowns.util.Util;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * Created by Frani on 06/03/2018.
 */
@Singleton
public class PlayerEvents {

    @Listener
    public void onJoin(ClientConnectionEvent.Join event, @Root Player p) {
        Resident resident = SpongyTowns.getInstance().getStorage().getOrLoadResident(p.getUniqueId());
        resident.updateLastActive();
    }

    @Listener
    public void onQuit(ClientConnectionEvent.Disconnect event, @Root Player p) {
        Resident resident = SpongyTowns.getManager().getResidents().get(p.getUniqueId());
        resident.updateLastActive();
    }

    @Listener
    public void onFeather(InteractItemEvent.Secondary.MainHand event, @Root Player player) {
        if (event.getItemStack().getType() != ItemTypes.FEATHER) return;
        if (!player.hasPermission(Permissions.FEATHER)) return;
        boolean sneaking = player.get(Keys.IS_SNEAKING).orElse(false);

        BlockRayHit<World> ray = BlockRay.from(player)
                .distanceLimit(30)
                .skipFilter(BlockRay.onlyAirFilter())
                .build().end().orElse(null);
        if (ray == null) return;

        if (sneaking) {
            Vector3i center = player.getLocation().getChunkPosition();
            int nearChunks = Util.getIntOption(player, Options.FEATHER_NEAR_CHUNKS);
            for (int x = -nearChunks; x < nearChunks; x++) {
                for (int z = -nearChunks; z < nearChunks; z++) {
                    Vector3i current = new Vector3i(center.getX() + x, center.getY(), center.getZ() + z);
                    Optional<TownClaim> claim = SpongyTowns.getManager().getClaimAt(current, player.getLocation().getExtent().getUniqueId());
                    claim.ifPresent(c -> Visual.startOrMerge(c.getParent(), player));
                }
            }
        } else {
            Optional<TownClaim> opt = SpongyTowns.getManager().getClaimAt(ray.getLocation());
            if (!opt.isPresent()) {
                player.sendMessage(TownMessages.getInstance().CHUNK_NOT_CLAIMED);
                return;
            }
            opt.get().getParent().sendInfo(player);
            Visual.startOrMerge(opt.get().getParent(), player);
        }
    }

}
