package io.github.eufranio.spongytowns.permission;

import lombok.Getter;
import lombok.NonNull;

/**
 * Created by Frani on 02/03/2018.
 */
public class Options {

    public static OptionEntry<Integer> CREATION_COST = OptionEntry.of("spongytowns.cost.create", 100);

    public static OptionEntry<Integer> PLOT_CREATION_COST = OptionEntry.of("spongytowns.cost.plot.create", 50);

    public static OptionEntry<Integer> PLOT_CLAIM_COST = OptionEntry.of("spongytowns.cost.claim.plot", 30);

    public static OptionEntry<Integer> CLAIM_COST = OptionEntry.of("spongytowns.cost.claim.town", 50);

    public static OptionEntry<Integer> CLAIM_COST_OUTPOST = OptionEntry.of("spongytowns.cost.claim.outpost", 150);

    public static OptionEntry<Integer> INITIAL_BANK_AMOUNT = OptionEntry.of("spongytowns.bank.initial-amount", 100);

    public static OptionEntry<Integer> CHUNKS_BETWEEN_TOWNS = OptionEntry.of("spongytowns.town.claim.chunks-between-towns", 4);

    public static OptionEntry<Integer> FEATHER_NEAR_CHUNKS = OptionEntry.of("spongytowns.misc.feather-near-chunks", 4);

    public static OptionEntry<Integer> DAILY_TAX_TOWN = OptionEntry.of("spongytowns.tax.daily.town", 200);

    public static OptionEntry<Integer> DAILY_TAX_TOWNCLAIM = OptionEntry.of("spongytowns.tax.daily.townclaim", 20);

    public static OptionEntry<Integer> DAILY_TAX_PLOT = OptionEntry.of("spongytowns.tax.daily.plot", 50);

    public static OptionEntry<Integer> DAILY_TAX_PLOTCLAIM = OptionEntry.of("spongytowns.tax.daily.plotclaim", 10);

    public static OptionEntry<Integer> DAILY_TAX_RESIDENT = OptionEntry.of("spongytowns.tax.daily.resident", 15);

    @Getter
    public static class OptionEntry<T> {

        private OptionEntry(String option, T defaultValue) {
            this.option = option;
            this.defaultValue = defaultValue;
        }

        public static <T> OptionEntry<T> of(String option, T defaultValue) {
            return new OptionEntry<>(option, defaultValue);
        }

        @NonNull
        public String option;

        @NonNull
        public T defaultValue;
    }

}
