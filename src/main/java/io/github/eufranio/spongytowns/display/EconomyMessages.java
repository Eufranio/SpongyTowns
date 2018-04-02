package io.github.eufranio.spongytowns.display;

import com.google.inject.Singleton;
import io.github.eufranio.spongytowns.SpongyTowns;
import lombok.Getter;
import lombok.Setter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by Frani on 02/03/2018.
 */
@Singleton
@ConfigSerializable
public class EconomyMessages {

    public static EconomyMessages getInstance() {
        return SpongyTowns.provide(EconomyMessages.class);
    }

    @Setting
    public TextTemplate COULD_NOT_WITHDRAW_TOWN = TextTemplate.of(
        "Couldn't withdraw the money from the Town bank: ", TextTemplate.arg("reason")
    );

    @Setting
    public TextTemplate NO_FUNDS = TextTemplate.of(
            TextColors.RED, "You don't have enough money to do this! ($", TextTemplate.arg("price").color(TextColors.GOLD), ")"
    );

    @Setting
    public TextTemplate ERROR_WITHDRAW = TextTemplate.of(
            TextColors.RED, "Error on withdraw: ", TextTemplate.arg("reason")
    );

    @Getter
    @Setting("transaction-reasons")
    public Reasons reasons = new Reasons();

    @ConfigSerializable
    public static class Reasons {

        @Setting
        public TextTemplate CLAIM = TextTemplate.of("Claim");

    }

}
