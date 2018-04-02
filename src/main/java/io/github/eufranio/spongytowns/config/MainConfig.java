package io.github.eufranio.spongytowns.config;

import com.google.inject.Singleton;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Created by Frani on 27/01/2018.
 */
@Getter
@Singleton
@ConfigSerializable
public class MainConfig {

    @Setting
    public String sqlURL = "jdbc:sqlite:SpongyTowns.db";

    @Setting
    public boolean economyEnabled = false;

    @Setting
    public int daysInactiveBeforePurge = 30;

    @Setting
    public int taxApplyHours = -1;

}
