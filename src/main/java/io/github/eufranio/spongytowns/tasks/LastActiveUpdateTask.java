package io.github.eufranio.spongytowns.tasks;

import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.permission.Resident;
import java.util.UUID;

/**
 * Created by Frani on 14/03/2018.
 */
public class LastActiveUpdateTask implements Runnable {

    @Override
    public void run() {
        for (Claim claim : SpongyTowns.getManager().getClaims()) {
            for (UUID uuid : claim.getMembers()) {
                Resident res = SpongyTowns.getManager().getResidents().get(uuid);
                if (res != null) {
                    if (res.getLastActiveInstant().isAfter(claim.getLastActiveInstant())) {
                        claim.setLastActive(res.getLastActive());
                    }
                }
            }
        }
    }

}
