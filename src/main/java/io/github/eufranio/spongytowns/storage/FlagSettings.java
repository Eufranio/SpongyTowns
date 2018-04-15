package io.github.eufranio.spongytowns.storage;

import com.google.common.collect.Maps;
import io.github.eufranio.spongytowns.interfaces.Persistant;
import io.github.eufranio.spongytowns.permission.Flags;
import lombok.Getter;
import lombok.Setter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Map;

/**
 * Created by Frani on 14/04/2018.
 */
@Getter
@Setter
@ConfigSerializable
public class FlagSettings extends SerializableObject<FlagSettings> {

    @Setting
    public Map<Flags, Boolean> flags = Maps.newHashMap();

    public FlagSettings() {
        super(FlagSettings.class);
        if (flags.isEmpty()) {
            for (Flags f : Flags.values()) {
                flags.put(f, f.getDefault());
            }
        }
    }

    public void save(Persistant obj) {
        obj.writeCustomData(DataKeys.FLAGS, this);
    }

}
