package io.github.eufranio.spongytowns.storage;

import io.github.eufranio.spongytowns.interfaces.Persistant;
import lombok.Getter;
import lombok.Setter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Created by Frani on 19/03/2018.
 */
@Getter
@Setter
@ConfigSerializable
public class TaxSettings extends SerializableObject<TaxSettings> {

    public TaxSettings() {
        super(TaxSettings.class);
    }

    @Setting
    public int dueDays = 0;

    @Setting
    public int remainingFrozenDays = -1;

    @Setting
    public boolean frozen = false;

    public void save(Persistant obj) {
        obj.writeCustomData(DataKeys.TAX, this);
    }

}
