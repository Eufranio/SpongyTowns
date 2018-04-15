package io.github.eufranio.spongytowns.managers;

import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.interfaces.ClaimBlock;
import io.github.eufranio.spongytowns.interfaces.Identifiable;
import io.github.eufranio.spongytowns.interfaces.Persistant;
import io.github.eufranio.spongytowns.permission.Resident;
import io.github.eufranio.spongytowns.storage.Transaction;
import io.github.eufranio.spongytowns.towns.*;
import lombok.Getter;
import org.spongepowered.api.Sponge;

import java.sql.*;
import java.util.*;

/**
 * Created by Frani on 27/01/2018.
 */
@Singleton
public class StorageManager {

    private ConnectionSource src;

    @Getter
    private Dao<Town, UUID> towns;

    @Getter
    private Dao<TownClaim, UUID> townClaims;

    @Getter
    private Dao<Plot, UUID> plots;

    @Getter
    private Dao<PlotClaim, UUID> plotClaims;

    @Getter
    private Dao<Resident, UUID> residents;

    @Getter
    private Dao<Transaction, UUID> transactions;

    public StorageManager(String url) {
        try {
            this.src = new JdbcConnectionSource(url);
            this.towns = DaoManager.createDao(src, Town.class);
            TableUtils.createTableIfNotExists(src, Town.class);

            this.townClaims = DaoManager.createDao(src, TownClaim.class);
            TableUtils.createTableIfNotExists(src, TownClaim.class);

            this.plots = DaoManager.createDao(src, Plot.class);
            TableUtils.createTableIfNotExists(src, Plot.class);

            this.plotClaims = DaoManager.createDao(src, PlotClaim.class);
            TableUtils.createTableIfNotExists(src, PlotClaim.class);

            this.residents = DaoManager.createDao(src, Resident.class);
            TableUtils.createTableIfNotExists(src, Resident.class);

            this.transactions = DaoManager.createDao(src, Transaction.class);
            TableUtils.createTableIfNotExists(src, Transaction.class);

            this.loadTowns();
            this.loadTransactions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadTowns() {
        int[] count = new int[] {0, 0, 0, 0};
        try {
            for (Town town : this.towns.queryForAll()) {
                count[0]++;

                for (TownClaim claim : this.townClaims.queryForEq("townId", town.getUniqueId())) {
                    count[1]++;
                    town.getBlocks().add(claim);
                    claim.setParent(town);
                    SpongyTowns.getManager().getTownClaims().put(claim.getUniqueId(), claim);
                }

                this.loadAndPurgeResidents(town);

                for (Plot plot : this.plots.queryForEq("townId", town.getUniqueId())) {
                    count[2]++;
                    plot.setParent(town);
                    for (PlotClaim plotClaim : this.plotClaims.queryForEq("plotId", plot.getUniqueId())) {
                        count[3]++;
                        plot.getBlocks().add(plotClaim);
                        plotClaim.setParent(plot);
                        plotClaim.setTown(town);
                        SpongyTowns.getManager().getPlotClaims().put(plotClaim.getUniqueId(), plotClaim);
                    }

                    this.loadAndPurgeResidents(plot);

                    town.getChilds().add(plot);
                    SpongyTowns.getManager().getPlots().put(plot.getUniqueId(), plot);
                }

                SpongyTowns.getManager().getTowns().put(town.getUniqueId(), town);
            }

            SpongyTowns.getInstance().getLogger().info(String.format("Loaded %s towns, %s town chunks, %s plot and %s plot chunks!", count[0], count[1], count[2], count[3]));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unchecked", "Duplicates"})
    public void save(Persistant obj) {
        try {
            if (obj.get() instanceof Claim) {
                Claim claim = (Claim) obj.get();
                if (claim.hasChilds()) {
                    claim.getChilds().forEach(this::save);
                }
            }
            if (obj.get().getDao().idExists(obj.getUniqueId())) {
                obj.get().update();
            } else {
                obj.get().create();
            }
            //obj.get().update();
            this.getUpdateMap(obj).putIfAbsent(obj.getUniqueId(), obj);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveTransaction(Transaction t) {
        try {
            if (t.getDao().idExists(t.getUniqueId())) {
                t.update();
            } else {
                t.create();
            }
            SpongyTowns.getManager().getTransactions().add(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unchecked", "Duplicates"})
    public void remove(Persistant obj) {
        try {
            if (obj.get() instanceof Claim) {
                Claim claim = (Claim) obj.get();
                if (claim.hasChilds()) {
                    claim.getChilds().forEach(this::remove);
                }
                if (claim.hasParent()) {
                    claim.getParent().getChilds().remove(claim);
                }
                claim.getBlocks().forEach(this::remove);
                claim.getMembers().forEach(uuid -> SpongyTowns.getManager().getResidents().get(uuid).remove(claim));
                if (SpongyTowns.isEconomyEnabled()) claim.resetAccount();
            }
            obj.get().delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.getUpdateMap(obj).remove(obj.getUniqueId());
    }

    public void saveTowns() {
        for (Claim town : SpongyTowns.getManager().getTowns().values()) {
            this.save(town);
        }
    }

    private Map getUpdateMap(Persistant obj) {
        Map updateMap;
        if (obj instanceof Town) {
            updateMap = SpongyTowns.getManager().getTowns();
        } else if (obj instanceof TownClaim) {
            updateMap = SpongyTowns.getManager().getTownClaims();
        } else if (obj instanceof Plot) {
            updateMap = SpongyTowns.getManager().getPlots();
        } else if (obj instanceof Resident) {
            updateMap = SpongyTowns.getManager().getResidents();
        } else {
            updateMap = SpongyTowns.getManager().getPlotClaims();
        }
        return updateMap;
    }

    public Resident getOrLoadResident(UUID uuid) {
        Resident r = SpongyTowns.getManager().getResidents().get(uuid);
        if (r == null) {
            try {
                r = this.residents.queryForId(uuid);
                if (r != null) {
                    r.setDao(this.residents);
                } else {
                    r = new Resident();
                    r.setId(uuid);
                    r.setPlots(new ArrayList<>());
                    r.setTowns(new ArrayList<>());
                    r.setCustomData(new HashMap<>());
                    r.setDao(this.residents);
                    r.create();
                }
                SpongyTowns.getManager().getResidents().put(uuid, r);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return r;
    }

    private void loadAndPurgeResidents(Claim claim) {
        if (claim.getMembers() != null) {
            List<UUID> residentsToPurge = Lists.newArrayList();
            claim.getMembers().forEach(uuid -> {
                if (this.getOrLoadResident(uuid) == null) {
                    residentsToPurge.add(uuid);
                }
            });
            if (!residentsToPurge.isEmpty()) {
                residentsToPurge.forEach(claim::removeMember);
                residentsToPurge.clear();
            }
        }
    }

    private void loadTransactions() {
        try {
            for (Transaction t : this.transactions.queryForAll()) {
                SpongyTowns.getManager().getTransactions().add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
