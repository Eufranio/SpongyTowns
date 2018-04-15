package io.github.eufranio.spongytowns.config;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Created by Frani on 27/01/2018.
 */
@Getter
@ConfigSerializable
public class MainConfig {

    @Setting
    public String sqlURL = "jdbc:sqlite:SpongyTowns.db";

    @Setting
    public boolean economyEnabled = false;

    @Setting
    public int daysInactiveBeforePurge = 90;

    @Setting
    public int frozenDaysBeforePurge = 7;

    @Setting
    public int dueDaysToFreezeClaim = 7;

    @Setting
    public int taxApplyHours = -1;

    @Setting(comment = "Should SpongyTowns show a message when entering a claim?")
    public boolean useGreetingMessage = true;

}
