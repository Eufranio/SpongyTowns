package io.github.eufranio.spongytowns.managers;

import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import io.github.eufranio.spongytowns.interfaces.Team;

import java.util.List;

/**
 * Created by Frani on 08/03/2018.
 */
@Singleton
public class TeamsManager {

    private static List<Team> teams = Lists.newArrayList();

    public static boolean teamsEnabled() {
        return !teams.isEmpty();
    }

    public static Team getTeam(String name) {
        return teams
                .stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public static void registerTeam(Team team) {
        teams.add(team);
    }

}
