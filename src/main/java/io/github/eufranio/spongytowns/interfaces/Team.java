package io.github.eufranio.spongytowns.interfaces;

import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.UUID;

/**
 * Created by Frani on 08/03/2018.
 */
public interface Team {

    Text getDisplayName();

    String getName();

    List<UUID> getMembers();

    UUID getOwner();

    Account getAccount();

}
