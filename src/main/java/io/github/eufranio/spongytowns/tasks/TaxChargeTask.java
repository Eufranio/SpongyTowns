package io.github.eufranio.spongytowns.tasks;

import com.google.common.collect.ImmutableMap;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.config.LastTaskRunConfig;
import io.github.eufranio.spongytowns.display.EconomyMessages;
import io.github.eufranio.spongytowns.display.TownMessages;
import io.github.eufranio.spongytowns.interfaces.Bank;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.permission.Options;
import io.github.eufranio.spongytowns.permission.Resident;
import io.github.eufranio.spongytowns.storage.DataKeys;
import io.github.eufranio.spongytowns.storage.TaxSettings;
import io.github.eufranio.spongytowns.towns.Town;
import io.github.eufranio.spongytowns.util.Util;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

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
            LastTaskRunConfig.getInstance().lastTaxTaskRun = Instant.now();
            SpongyTowns.log("Starting the tax apply task");
            for (Claim claim : SpongyTowns.getManager().getClaims()) {
                if (claim.isAdmin()) continue;
                tryToCharge(claim);
            }
        }
    }

    private static void tryToCharge(Claim claim) {
        TaxSettings taxSettings = (TaxSettings) claim.get(DataKeys.TAX);

        int value = claim.getTax();
        Resident owner = SpongyTowns.getManager().getResidents().get(claim.getOwner());
        if (taxSettings.getDueDays() != 0 && taxSettings.getDueDays() < SpongyTowns.getConfig().getDueDaysToFreezeClaim()) {
            value = value * taxSettings.getDueDays();
            taxSettings.dueDays++;
            if (taxSettings.getDueDays() < SpongyTowns.getConfig().getDueDaysToFreezeClaim()) {
                owner.queueMessage(TownMessages.getInstance().CLAIM_WILL_FREEZE.apply(ImmutableMap.of(
                        "claim", claim.getInfoHover(),
                        "days", Text.of(TextColors.GOLD, SpongyTowns.getConfig().getDueDaysToFreezeClaim() - taxSettings.getDueDays())
                )).toText());
            }
        }

        if (taxSettings.getDueDays() >= SpongyTowns.getConfig().getDueDaysToFreezeClaim()) {
            if (taxSettings.getRemainingFrozenDays() == -1) {
                taxSettings.setRemainingFrozenDays(SpongyTowns.getConfig().getFrozenDaysBeforePurge());
                taxSettings.setFrozen(true);
            }
            if (taxSettings.isFrozen()) {
                taxSettings.remainingFrozenDays--;
                if (taxSettings.getRemainingFrozenDays() > 0) {
                    owner.queueMessage(TownMessages.getInstance().CLAIM_HAS_FROZEN.apply(ImmutableMap.of(
                            "claim", claim.getInfoHover(),
                            "days", Text.of(TextColors.GOLD, taxSettings.getRemainingFrozenDays())
                    )).toText());
                }
                if (taxSettings.getRemainingFrozenDays() == 0) {
                    owner.queueMessage(TownMessages.getInstance().CLAIM_HAS_EXPIRED.apply(ImmutableMap.of(
                            "claim", claim.getInfoHover()
                    )).toText());
                    taxSettings.save(claim);
                    claim.remove();
                    return;
                }
            }
        }

        boolean success = claim.getBank().withdraw(Bank.server(), value, EconomyMessages.getInstance().getReasons().TAX.toText(), null) == ResultType.SUCCESS;
        if (!success) {
            taxSettings.dueDays++;
        }
        taxSettings.save(claim);
    }

}
