package io.github.eufranio.spongytowns.display;

import com.google.inject.Singleton;
import io.github.eufranio.spongytowns.SpongyTowns;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

/**
 * Created by Frani on 14/03/2018.
 */
@Singleton
@ConfigSerializable
public class BankMessages {

    public static BankMessages getInstance() {
        return SpongyTowns.provide(BankMessages.class);
    }

    @Getter
    @Setting
    public Info info = new Info();

    @ConfigSerializable
    public static class Info {

        @Setting
        public TextTemplate BALANCE = TextTemplate.of(
                TextColors.YELLOW, "Balance: $", TextTemplate.arg("balance").color(TextColors.GOLD)
        );

        @Setting
        public TextTemplate TRANSACTIONS = TextTemplate.of(
                TextColors.GOLD, "Last transactions: ", TextTemplate.arg("transactions").color(TextColors.GREEN).style(TextStyles.UNDERLINE)
        );

    }

}
