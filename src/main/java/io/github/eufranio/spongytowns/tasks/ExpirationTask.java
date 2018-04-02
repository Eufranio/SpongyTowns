package io.github.eufranio.spongytowns.tasks;

import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.interfaces.Claim;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Created by Frani on 14/03/2018.
 */
public class ExpirationTask implements Runnable {

    @Override
    public void run() {
        for (Claim claim : SpongyTowns.getManager().getClaims()) {
            Instant now = Instant.now();
            long days = ChronoUnit.DAYS.between(claim.getLastActiveInstant(), now);
            if (days >= SpongyTowns.getConfig().getDaysInactiveBeforePurge()) {
                SpongyTowns.log("The claim " + claim.getName() + " has enough inactive days and will be deleted!");
                claim.remove();
            }
        }
    }

}
