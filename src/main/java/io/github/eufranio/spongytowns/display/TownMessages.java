package io.github.eufranio.spongytowns.display;

import com.google.inject.Singleton;
import io.github.eufranio.spongytowns.SpongyTowns;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by Frani on 24/02/2018.
 */
@Singleton
@ConfigSerializable
public class TownMessages {

    public static TownMessages getInstance() {
        return SpongyTowns.provide(TownMessages.class);
    }

    @Setting
    public TextTemplate CREATE = TextTemplate.of(
            TextColors.GREEN, " The claim ", TextTemplate.arg("town"), TextColors.GREEN, " was successfully created!"
    );

    @Setting
    public TextTemplate CLAIM = TextTemplate.of(
            TextColors.GREEN, "Successfully claimed this chunk to ", TextTemplate.arg("town")
    );

    @Setting
    public TextTemplate UNCLAIM = TextTemplate.of(
            TextColors.GREEN, "Successfully unclaimed this chunk!"
    );

    @Setting
    public TextTemplate ABOUT_TO_DELETE = TextTemplate.of(
            TextColors.RED, " You're about to delete ", TextTemplate.arg("claim"), " and ALL it's chunks/subclaims! ",
            TextTemplate.arg("button")
    );

    @Setting
    public TextTemplate DELETE = TextTemplate.of(
            TextColors.RED, " You just deleted ", TextTemplate.arg("claim"), TextColors.RED, " and all it's subclaims!"
    );

    @Setting
    public TextTemplate ALREADY_CLAIMED = TextTemplate.of(
            TextColors.RED, " This chunk belongs to ", TextTemplate.arg("town"), TextColors.RED, "!"
    );

    @Setting
    public TextTemplate CHUNK_NOT_CLAIMED = TextTemplate.of(
            TextColors.RED, " There's no claim at this chunk!"
    );

    @Setting
    public TextTemplate NAME_USED = TextTemplate.of(
            TextColors.RED, " This name is already used by other town!"
    );

    @Setting
    public TextTemplate OUTPOST_NO_NAME = TextTemplate.of(
            "If you're trying to claim outposts, you must specify the name of the parent town in the command!"
    );

    @Setting
    public TextTemplate NO_FUNDS_CLAIM = TextTemplate.of(
            "The Town ", TextTemplate.arg("town"), " doesn't have enough funds to claim ($", TextTemplate.arg("price").color(TextColors.GOLD), ")!"
    );

    @Setting
    public TextTemplate TOWNS_NEAR = TextTemplate.of(
            TextColors.RED, "There are ", TextTemplate.arg("count").color(TextColors.GOLD),
            " town(s) near you! You cannot claim within ", TextTemplate.arg("chunks").color(TextColors.GOLD), " chunks of other towns!",
            Text.NEW_LINE,
            "Near towns: ", TextTemplate.arg("towns")
    );

    @Setting
    public TextTemplate UNCLAIM_LAST_CHUNK = TextTemplate.of(
            TextColors.RED, " This is the last town block of ", TextTemplate.arg("town"), ", unclaiming it will result in deleting " +
                    "the town at all! Are you sure you want to do that? ", TextTemplate.arg("button")
    );

    @Setting("town-info")
    public TownInfo TOWN_INFO = new TownInfo();

    @ConfigSerializable
    public static class TownInfo {

        @Setting
        public TextTemplate NAME = TextTemplate.of(
                TextColors.AQUA, "Name: ", TextTemplate.arg("name").color(TextColors.GOLD)
        );

        @Setting
        public TextTemplate OWNER = TextTemplate.of(
                TextColors.AQUA, "Owner: ", TextTemplate.arg("owner").color(TextColors.GOLD)
        );

        @Setting
        public TextTemplate CHUNKS = TextTemplate.of(
                TextColors.AQUA, "Claimed chunks: ", TextTemplate.arg("chunks").color(TextColors.GOLD)
        );

        @Setting
        public TextTemplate PLOTS = TextTemplate.of(
                TextColors.AQUA, "Plots: ", TextTemplate.arg("plots").color(TextColors.GOLD)
        );

        @Setting
        public TextTemplate RESIDENTS = TextTemplate.of(
                TextColors.AQUA, "Residents: ", TextTemplate.arg("residents").color(TextColors.GOLD)
        );

    }

    @Setting
    public TextTemplate CLAIM_WILL_FREEZE = TextTemplate.of(
        TextColors.RED, "The Claim ", TextTemplate.arg("claim"), " has no money and will be freezed in ",
            TextTemplate.arg("days"), " days! You won't be able to interact with it while it's frozen. " +
                    "Pay your taxes if you don't want that to happen!"
    );

    @Setting
    public TextTemplate CLAIM_HAS_FROZEN = TextTemplate.of(
            TextColors.RED, "The Claim ", TextTemplate.arg("claim"), " has no money and is now frozen. You cannot interact with it " +
                    "unless you pay it's taxes. It will be completely deleted in ", TextTemplate.arg("days"), " days!"
    );

    @Setting
    public TextTemplate CLAIM_HAS_EXPIRED = TextTemplate.of(
            TextColors.RED, "The Claim ", TextTemplate.arg("claim"), " has not paid it's taxes and was deleted!"
    );

    @Setting
    public TextTemplate UNKNOWN_CLAIM = TextTemplate.of(
            TextColors.RED, "If you're not inside a claim, you must specify one in the command!"
    );

    @Setting
    public TextTemplate ENTERING_CLAIM = TextTemplate.of(
            TextColors.GREEN, "You're now entering ", TextTemplate.arg("claim"), "!"
    );

    @Setting
    public TextTemplate CHANGED_OWNER = TextTemplate.of(
            TextColors.GREEN, "Successfully changed owner of ", TextTemplate.arg("claim"), "!"
    );

}
