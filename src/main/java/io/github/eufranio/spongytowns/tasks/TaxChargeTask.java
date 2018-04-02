package io.github.eufranio.spongytowns.tasks;

import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.config.LastTaskRunConfig;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.permission.Options;
import io.github.eufranio.spongytowns.storage.DataKeys;
import io.github.eufranio.spongytowns.storage.TaxSettings;
import io.github.eufranio.spongytowns.towns.Town;
import io.github.eufranio.spongytowns.util.Util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Created by Frani on 19/03/2018.
 */
public class TaxChargeTask implements Runnable {

    @Override
    public void run() {
        Instant lastRun = LastTaskRunConfig.getInstance().getLastTaxTaskRun();
        if (lastRun == null || ChronoUnit.HOURS.between(lastRun, Instant.now()) >= SpongyTowns.getConfig().getTaxApplyHours()) {
            SpongyTowns.log("Starting the tax apply task");
            for (Claim claim : SpongyTowns.getManager().getClaims()) {
                if (claim.isAdmin()) continue;

            }
        }
    }

    private static boolean tryToCharge(Claim claim) {
        TaxSettings taxSettings = (TaxSettings) claim.get(DataKeys.TAX);
        Options.OptionEntry<Integer> entry = claim instanceof Town ? Options.DAILY_TAX_TOWN : Options.DAILY_TAX_PLOT;
        int value = Util.getIntOption(claim.getOwnerUser(), entry);
        if (taxSettings.getDueDays() != 0) {
            value = value * taxSettings.getDueDays();
            taxSettings.dueDays++;
            
        }
    }

}
