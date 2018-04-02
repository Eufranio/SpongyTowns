package io.github.eufranio.spongytowns.interfaces;

import io.github.eufranio.spongytowns.util.Util;
import org.spongepowered.api.entity.living.player.User;

import java.util.List;
import java.util.UUID;

/**
 * Created by Frani on 20/02/2018.
 */
public interface Permissible {

    List<UUID> getMembers();

    void addMember(UUID uuid);

    void removeMember(UUID uuid);

    UUID getOwner();

    default User getOwnerUser() {
        return Util.getUser(this.getOwner());
    }

    void setOwner(UUID owner);

}
