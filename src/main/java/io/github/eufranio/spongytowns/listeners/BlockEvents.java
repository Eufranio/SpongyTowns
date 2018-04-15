package io.github.eufranio.spongytowns.listeners;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Singleton;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.display.PermissionMessages;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.util.Util;
import net.minecraft.block.BlockLiquid;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.property.block.MatterProperty;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.NotifyNeighborBlockEvent;
import org.spongepowered.api.event.cause.EventContextKey;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Frani on 13/04/2018.
 */
@Singleton
public class BlockEvents {

    // from GP
    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onNotifyNeighbour(NotifyNeighborBlockEvent event) {
        LocatableBlock locatableBlock = event.getCause().first(LocatableBlock.class).orElse(null);
        TileEntity tileEntity = event.getCause().first(TileEntity.class).orElse(null);
        Location<World> sourceLocation = locatableBlock != null ? locatableBlock.getLocation() : tileEntity != null ? tileEntity.getLocation() : null;
        UUID creator = sourceLocation.getExtent().getCreator(sourceLocation.getBlockPosition()).orElse(null);
        if (creator == null) {
            // check notifier
            creator = sourceLocation.getExtent().getNotifier(sourceLocation.getBlockPosition()).orElse(null);
        }
        if (creator != null) {
            User creatorUser = Util.getUser(creator);
            Iterator<Direction> iterator = event.getNeighbors().keySet().iterator();
            while (iterator.hasNext()) {
                Direction d = iterator.next();
                Location<World> relative = sourceLocation.getBlockRelative(d);
                Claim at = SpongyTowns.getManager().getClaimAt(relative).orElse(null);
                if (at != null && !at.hasPermission(creatorUser)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onChangeBlock(ChangeBlockEvent event) {
        User player = Util.getEventUser(event);
        if (player == null) {
            return;
        }

        boolean cancel = false;
        Claim claim = null;
        for (Transaction<BlockSnapshot> t : event.getTransactions()) {
            Location<World> loc = t.getOriginal().getLocation().get();
            Claim at = SpongyTowns.getManager().getClaimAt(loc).orElse(null);
            if (at != null) {
                // TODO: use flags
                if (!at.hasPermission(player)) {
                    t.setValid(false);
                    cancel = true;
                    claim = at;
                }
            }
        }
        if (cancel) {
            if (player.isOnline()) {
                player.getPlayer().get().sendMessage(PermissionMessages.getInstance().CANNOT_BUILD.apply(ImmutableMap.of(
                        "claim", claim.getInfoHover()
                )).toText());
            }
            event.setCancelled(true);
        }
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onInteractBlock(InteractBlockEvent event) {
        event.getTargetBlock().getLocation().ifPresent(loc ->
                SpongyTowns.getManager().getClaimAt(loc).ifPresent(c -> {
                    User user = Util.getEventUser(event);
                    if (user != null && !c.hasPermission(user)) {
                        if (user.isOnline()) {
                            c.sendDenyMessage(PermissionMessages.getInstance().CANNOT_INTERACT_BLOCK, user.getPlayer().get());
                        }
                        event.setCancelled(true);
                    }
                }));
    }

}
