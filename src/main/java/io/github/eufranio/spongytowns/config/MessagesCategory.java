package io.github.eufranio.spongytowns.config;

import io.github.eufranio.spongytowns.display.*;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Created by Frani on 24/02/2018.
 */
@Getter
@ConfigSerializable
public class MessagesCategory {

    @Setting
    public TownMessages town = new TownMessages();

    @Setting
    public PermissionMessages permissions = new PermissionMessages();

    @Setting
    public EconomyMessages economy = new EconomyMessages();

    @Setting
    public BankMessages bank = new BankMessages();

    @Setting
    public ResidentMessages res = new ResidentMessages();

}
