package io.github.eufranio.spongytowns.storage;

/**
 * Created by Frani on 19/03/2018.
 */
public class DataKeys {

    public static DataKey<TaxSettings> TAX = DataKey.of("taxes", TaxSettings.class);

    public static DataKey<FlagSettings> FLAGS = DataKey.of("flags", FlagSettings.class);

    public static DataKey<InviteSettings> INVITES = DataKey.of("invites", InviteSettings.class);

}
