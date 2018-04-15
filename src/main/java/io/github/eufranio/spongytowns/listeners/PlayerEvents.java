package io.github.eufranio.spongytowns.listeners;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Singleton;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.display.PermissionMessages;
import io.github.eufranio.spongytowns.display.ResidentMessages;
import io.github.eufranio.spongytowns.display.TownMessages;
import io.github.eufranio.spongytowns.display.visual.BorderVisual;
import io.github.eufranio.spongytowns.display.visual.Visual;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.interfaces.ClaimBlock;
import io.github.eufranio.spongytowns.permission.Flags;
import io.github.eufranio.spongytowns.permission.Options;
import io.github.eufranio.spongytowns.permission.Permissions;
import io.github.eufranio.spongytowns.permission.Resident;
import io.github.eufranio.spongytowns.storage.DataKeys;
import io.github.eufranio.spongytowns.storage.FlagSettings;
import io.github.eufranio.spongytowns.storage.InviteSettings;
import io.github.eufranio.spongytowns.towns.TownClaim;
import io.github.eufranio.spongytowns.util.Util;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Frani on 06/03/2018.
 */
@Singleton
public class PlayerEvents {

    @Listener
    public void onJoin(ClientConnectionEvent.Join event, @Root Player p) {
        Resident resident = SpongyTowns.getStorage().getOrLoadResident(p.getUniqueId());
        resident.updateLastActive();

        InviteSettings i = (InviteSettings) resident.get(DataKeys.INVITES);
        if (!i.getInvites().isEmpty()) {
            p.sendMessage(ResidentMessages.getInstance().YOU_HAVE_INVITES.apply(ImmutableMap.of(
                    "count", i.getInvites().size()
            )).toText());
        }
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
                    Optional<TownClaim> claim = SpongyTowns.getManager().getClaimBlockAt(current, player.getLocation().getExtent().getUniqueId());
                    claim.ifPresent(c -> {
                        if (Visual.getVisuals().stream()
                                .filter(v -> v.viewers.contains(player.getUniqueId()))
                                .collect(Collectors.toList())
                                .size() >= 15) return;
                        Visual v = new BorderVisual(c.getParent(), player);
                        v.start();
                    });
                }
            }
        } else {
            Optional<ClaimBlock> opt = SpongyTowns.getManager().getClaimBlockAt(ray.getLocation());
            if (!opt.isPresent()) {
                player.sendMessage(TownMessages.getInstance().CHUNK_NOT_CLAIMED);
                return;
            }
            opt.get().getParent().sendInfo(player);
            if (Visual.getVisuals().stream()
                    .filter(v -> v.viewers.contains(player.getUniqueId()))
                    .collect(Collectors.toList())
                    .size() >= 15) return;
            Visual v = new BorderVisual(opt.get().getParent(), player);
            v.start();
        }
    }

    @Listener
    public void onMove(MoveEntityEvent e, @Root Player player) {
        // ignore head rotation
        if (e.getFromTransform()
                .getPosition().toInt()
                .equals(e.getToTransform().getPosition().toInt())) return;

        Claim fromClaim = SpongyTowns.getManager().getClaimAt(e.getFromTransform().getLocation()).orElse(null);
        Claim toClaim = SpongyTowns.getManager().getClaimAt(e.getToTransform().getLocation()).orElse(null);

        // handle greeting
        if ((fromClaim != null && toClaim != null && fromClaim != toClaim) || (fromClaim == null && toClaim != null)) {
            FlagSettings settings = (FlagSettings) toClaim.get(DataKeys.FLAGS);
            if (!settings.getFlags().get(Flags.ENTRY) && !toClaim.hasPermission(player)) {
                toClaim.sendDenyMessage(PermissionMessages.getInstance().CANNOT_ENTER, player);
                e.setCancelled(true);
                Visual v = new BorderVisual(toClaim, player);
                v.start();
                return;
            }
            if (SpongyTowns.getConfig().useGreetingMessage) {
                player.sendMessage(TownMessages.getInstance().ENTERING_CLAIM.apply(ImmutableMap.of(
                        "claim", toClaim.getInfoHover()
                )).toText());
            }
        }
    }

}
