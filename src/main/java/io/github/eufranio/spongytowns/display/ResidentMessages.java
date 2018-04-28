package io.github.eufranio.spongytowns.display;

import io.github.eufranio.spongytowns.SpongyTowns;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by Frani on 13/04/2018.
 */
@ConfigSerializable
public class ResidentMessages {

    public static ResidentMessages getInstance() {
        return SpongyTowns.provide(ResidentMessages.class);
    }

    @Getter
    @Setting
    public Info info = new Info();

    @ConfigSerializable
    public static class Info {

        @Setting
        public TextTemplate NAME = TextTemplate.of(
                TextColors.AQUA, "Name: ", TextTemplate.arg("name").color(TextColors.GOLD)
        );

        @Setting
        public TextTemplate UUID = TextTemplate.of(
                TextColors.AQUA, "UUID: ", TextTemplate.arg("uuid").color(TextColors.GOLD)
        );

        @Setting
        public TextTemplate OWNER = TextTemplate.of(
                TextColors.AQUA, "Owner: ", TextTemplate.arg("claims").color(TextColors.AQUA)
        );

        @Setting
        public TextTemplate MEMBER = TextTemplate.of(
                TextColors.AQUA, "Member in: ", TextTemplate.arg("claims")
        );

        @Setting
        public TextTemplate LAST_ACTIVE = TextTemplate.of(
                TextColors.AQUA, "Last active: ", TextTemplate.arg("date").color(TextColors.WHITE)
        );

        @Setting
        public TextTemplate FIRST_JOIN = TextTemplate.of(
                TextColors.AQUA, "First played: ", TextTemplate.arg("date").color(TextColors.WHITE)
        );

        @Setting
        public TextTemplate TEAM = TextTemplate.of(
                TextColors.AQUA, "Team: ", TextTemplate.arg("team").color(TextColors.BLUE)
        );

    }

    @Setting
    public TextTemplate RECEIVED_INVITE = TextTemplate.of(
            TextColors.GREEN, "Hey, you was invited to join ", TextTemplate.arg("claim"), "! ",
            TextTemplate.arg("clickHere"), " to accept this invite now, or accept/deny later using " +
                    " /town invites!"
    );

    @Setting
    public TextTemplate SENT_INVITE = TextTemplate.of(
            TextColors.GREEN, "Successfully sent invite to ", TextTemplate.arg("resident").color(TextColors.GOLD), "!"
    );

    @Setting
    public TextTemplate YOU_HAVE_INVITES = TextTemplate.of(
            TextColors.GREEN, "Hey, you have ", TextTemplate.arg("count").color(TextColors.GOLD),
            " pending invite(s)! Use /town invites to view and accept/deny them!"
    );

    @Setting
    public TextTemplate YOU_HAVE_NO_INVITES = TextTemplate.of(
            TextColors.RED, "You have no invites to show!"
    );

    @Setting
    public TextTemplate YOU_HAVE_MANY_INVITES = TextTemplate.of(
            TextColors.RED, "You have more than one invite, so you must accept/deny them via /town invites!"
    );

    @Setting
    public TextTemplate JOINED_TOWN = TextTemplate.of(
            TextColors.GREEN, "Successfully joined ", TextTemplate.arg("claim"), "!"
    );

    @Setting
    public TextTemplate DENIED_INVITE = TextTemplate.of(
            TextColors.RED, "Successfully denied invite from ", TextTemplate.arg("claim"), "!"
    );

    @Setting
    public TextTemplate NOT_PART_OF_CLAIM = TextTemplate.of(
            TextColors.RED, TextTemplate.arg("resident"), " is not part of ",
            TextTemplate.arg("claim"), "!"
    );

    @Setting
    public TextTemplate KICKED = TextTemplate.of(
            TextColors.GREEN, "Successfully kicked ", TextTemplate.arg("resident").color(TextColors.GOLD),
            " from ", TextTemplate.arg("claim"), "!"
    );

}
