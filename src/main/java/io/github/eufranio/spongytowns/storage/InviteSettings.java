package io.github.eufranio.spongytowns.storage;

import com.google.common.collect.Lists;
import io.github.eufranio.spongytowns.interfaces.Persistant;
import lombok.Getter;
import lombok.Setter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Created by Frani on 14/04/2018.
 */
@Getter
@ConfigSerializable
public class InviteSettings extends SerializableObject<InviteSettings> {

    public InviteSettings() {
        super(InviteSettings.class);
    }

    @Setting
    public List<Invite> invites = Lists.newArrayList();

    @Getter
    @Setter
    @ConfigSerializable
    public static class Invite {

        @Setting
        public Instant sent;

        @Setting
        public UUID claim;

        @Setting
        public UUID sentBy;

    }

    public void save(Persistant obj) {
        obj.writeCustomData(DataKeys.INVITES, this);
    }

}
