package io.github.eufranio.spongytowns.config;

import com.google.inject.Singleton;
import io.github.eufranio.spongytowns.SpongyTowns;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.time.Instant;

/**
 * Created by Frani on 19/03/2018.
 */
@Getter
@ConfigSerializable
public class LastTaskRunConfig {

    public static LastTaskRunConfig getInstance() {
        return SpongyTowns.provide(LastTaskRunConfig.class);
    }

    @Setting
    public Instant lastTaxTaskRun;

}
