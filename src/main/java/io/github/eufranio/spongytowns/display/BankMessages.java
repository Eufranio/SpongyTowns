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
                TextColors.YELLOW, "Balance: ", TextTemplate.arg("balance").color(TextColors.DARK_GREEN)
        );

        @Setting
        public TextTemplate TAX = TextTemplate.of(
                TextColors.YELLOW, "Daily tax: ", TextTemplate.arg("tax").color(TextColors.DARK_GREEN)
        );

        @Setting
        public TextTemplate DUE_DAYS = TextTemplate.of(
                TextColors.YELLOW, "Due days: ", TextTemplate.arg("days").color(TextColors.GOLD)
        );

        @Setting
        public TextTemplate FROZEN = TextTemplate.of(
                TextColors.YELLOW, "Frozen: ", TextTemplate.arg("frozen").color(TextColors.GOLD)
        );

        @Setting
        public TextTemplate TRANSACTIONS = TextTemplate.of(
                TextColors.YELLOW, "Last transactions: ", TextTemplate.arg("transactions").color(TextColors.GREEN).style(TextStyles.UNDERLINE)
        );

    }

    @Setting
    public TextTemplate ONLY_MEMBERS_DEPOSIT = TextTemplate.of(
            TextColors.RED, "Only members/owner can deposit money to the bank!"
    );

    @Setting
    public TextTemplate NOT_ENOUGH_MONEY_DEPOSIT = TextTemplate.of(
            TextColors.RED, "You don't have enough money to deposit!"
    );

    @Setting
    public TextTemplate ONLY_OWNER_WITHDRAW = TextTemplate.of(
            TextColors.RED, "Only owners can withdraw money from the bank!"
    );

}
