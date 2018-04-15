package io.github.eufranio.spongytowns.listeners;

import com.google.inject.Singleton;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.display.PermissionMessages;
import io.github.eufranio.spongytowns.util.Util;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;

/**
 * Created by Frani on 13/04/2018.
 */
@Singleton
public class EntityEvents {

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onCollideBlock(CollideBlockEvent.Impact event) {
        SpongyTowns.getManager().getClaimAt(event.getTargetLocation()).ifPresent(c -> {
            User user = Util.getEventUser(event);
            if (user != null && !c.hasPermission(user)) {
                if (user.isOnline()) {
                    c.sendDenyMessage(PermissionMessages.getInstance().CANNOT_BUILD, user.getPlayer().get());
                }
                event.setCancelled(true);
            }
        });
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onCollideEntity(CollideEntityEvent.Impact event) {
        SpongyTowns.getManager().getClaimAt(event.getImpactPoint()).ifPresent(c -> {
            User user = Util.getEventUser(event);
            if (user != null && !c.hasPermission(user)) {
                if (user.isOnline()) {
                    c.sendDenyMessage(PermissionMessages.getInstance().ENTITY_PROTECTED, user.getPlayer().get());
                }
                event.setCancelled(true);
            }
        });
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onInteractEntity(InteractEntityEvent event) {
        SpongyTowns.getManager().getClaimAt(event.getTargetEntity().getLocation()).ifPresent(c -> {
            User user = Util.getEventUser(event);
            if (user != null && !c.hasPermission(user)) {
                if (user.isOnline()) {
                    c.sendDenyMessage(PermissionMessages.getInstance().ENTITY_PROTECTED, user.getPlayer().get());
                }
                event.setCancelled(true);
            }
        });
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onDamage(DamageEntityEvent event) {
        SpongyTowns.getManager().getClaimAt(event.getTargetEntity().getLocation()).ifPresent(c -> {
            User user = Util.getEventUser(event);
            if (user != null && !c.hasPermission(user)) {
                if (user.isOnline()) {
                    c.sendDenyMessage(PermissionMessages.getInstance().ENTITY_PROTECTED, user.getPlayer().get());
                }
                event.setCancelled(!c.hasPermission(user));
            }
        });
    }

}
