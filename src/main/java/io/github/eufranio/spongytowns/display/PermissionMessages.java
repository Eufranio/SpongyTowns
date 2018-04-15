package io.github.eufranio.spongytowns.display;

import io.github.eufranio.spongytowns.SpongyTowns;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by Frani on 24/02/2018.
 */
@ConfigSerializable
public class PermissionMessages {

    public static PermissionMessages getInstance() {
        return SpongyTowns.provide(PermissionMessages.class);
    }

    @Setting
    public TextTemplate NO_PERMISSION_COMMAND = TextTemplate.of(TextColors.RED, "You don't have permission to execute this command!");

    @Setting
    public TextTemplate NO_PERMISSION_OUTPOST = TextTemplate.of(TextColors.RED, "You don't have permission to make outpost claims!");

    @Setting
    public TextTemplate NO_PERMISSION_CLAIM = TextTemplate.of(
            TextColors.RED, "You don't have permission to claim to ", TextTemplate.arg("town"), TextColors.RED, "! Ask it's owner to claim or specify another town in the command!"
    );

    @Setting
    public TextTemplate NO_PERMISSION_UNCLAIM = TextTemplate.of(
            TextColors.RED, "Only the owner of ", TextTemplate.arg("town"), TextColors.RED, " can unclaim this chunk!"
    );

    @Setting
    public TextTemplate CANNOT_BUILD = TextTemplate.of(
            TextColors.RED, "You don't have permission to build here, this belongs to ", TextTemplate.arg("claim"), "!"
    );

    @Setting
    public TextTemplate ENTITY_PROTECTED = TextTemplate.of(
            TextColors.RED, "This entity belongs to ", TextTemplate.arg("claim"), "!"
    );

    @Setting
    public TextTemplate CANNOT_INTERACT_BLOCK = TextTemplate.of(
            TextColors.RED, "You don't have permission to interact here, this belongs to ", TextTemplate.arg("claim"), "!"
    );

    @Setting
    public TextTemplate CANNOT_ENTER = TextTemplate.of(
            TextColors.RED, "You don't have permission to enter in ", TextTemplate.arg("claim"), "!"
    );

    @Setting
    public TextTemplate ONLY_OWNERS = TextTemplate.of(
            TextColors.RED, "Only the owner of ", TextTemplate.arg("claim"), " can do that!"
    );

}
