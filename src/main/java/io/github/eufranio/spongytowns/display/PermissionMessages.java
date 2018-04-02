package io.github.eufranio.spongytowns.display;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import io.github.eufranio.spongytowns.SpongyTowns;
import lombok.Getter;
import lombok.Setter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by Frani on 24/02/2018.
 */
@Singleton
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

}
