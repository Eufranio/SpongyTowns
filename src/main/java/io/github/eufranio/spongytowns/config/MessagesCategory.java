package io.github.eufranio.spongytowns.config;

import io.github.eufranio.spongytowns.display.BankMessages;
import io.github.eufranio.spongytowns.display.EconomyMessages;
import io.github.eufranio.spongytowns.display.PermissionMessages;
import io.github.eufranio.spongytowns.display.TownMessages;
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

}
