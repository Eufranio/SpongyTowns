package io.github.eufranio.spongytowns.commands;

import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.permission.Permissions;
import lombok.Getter;

/**
 * Created by Frani on 24/02/2018.
 */
public enum Command {

    CREATE_TOWN (Permissions.CREATE_TOWN, "create");

    @Getter
    private String permission;
    private String name;

    Command(String permission, String name) {
        this.permission = permission;
        this.name = name;
    }

}
