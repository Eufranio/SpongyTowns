package io.github.eufranio.spongytowns.commands.resident;

import com.google.common.collect.ImmutableMap;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.display.ResidentMessages;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.managers.TeamsManager;
import io.github.eufranio.spongytowns.permission.Resident;
import io.github.eufranio.spongytowns.util.InfoBuilder;
import io.github.eufranio.spongytowns.util.Util;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.sql.Date;
import java.util.stream.Collectors;

/**
 * Created by Frani on 13/04/2018.
 */
public class InfoCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource sender, CommandContext context) throws CommandException {
        Resident res;
        if (context.<Resident>getOne("resident").isPresent()) {
            res = context.<Resident>getOne("resident").get();
        } else {
            if (!(sender instanceof Player)) {
                Util.error("If you're running this from console, specify a player!");
            }
            res = SpongyTowns.getManager().getResidents().get(((Player) sender).getUniqueId());
        }
        InfoBuilder.create()
                .title(res.getUser().getName() + "'s Resident Info")
                .add(ResidentMessages.getInstance().getInfo().NAME.apply(ImmutableMap.of(
                        "name", res.getUser().getName()
                )))
                .add(ResidentMessages.getInstance().getInfo().UUID.apply(ImmutableMap.of(
                        "uuid", res.getUniqueId()
                )))
                .add(ResidentMessages.getInstance().getInfo().OWNER.apply(ImmutableMap.of(
                        "claims", Text.joinWith(Text.of(TextColors.GRAY, ", "), SpongyTowns.getManager().getTowns()
                                .values()
                                .stream()
                                .filter(c -> c.getOwner().equals(res.id))
                                .map(Claim::getInfoHover)
                                .collect(Collectors.toList()))
                        )
                ))
                .add(ResidentMessages.getInstance().getInfo().MEMBER.apply(ImmutableMap.of(
                        "claims", Text.joinWith(Text.of(TextColors.GRAY, ", "), SpongyTowns.getManager().getClaims()
                                .stream()
                                .filter(c -> c.getMembers().contains(res.id))
                                .map(Claim::getInfoHover)
                                .collect(Collectors.toList()))
                        )
                ))
                .add(ResidentMessages.getInstance().getInfo().LAST_ACTIVE.apply(ImmutableMap.of(
                        "date", Date.from(res.getLastActiveInstant()).toGMTString()
                )))
                .add(ResidentMessages.getInstance().getInfo().FIRST_JOIN.apply(ImmutableMap.of(
                        "date", Date.from(res.getUser().require(JoinData.class).firstPlayed().get()).toGMTString()
                )))
                .add(ResidentMessages.getInstance().getInfo().TEAM.apply(ImmutableMap.of(
                        "team", TeamsManager.teamsEnabled() ?
                                TeamsManager.getTeam(res.getTown().getTeam()) != null ?
                                        TeamsManager.getTeam(res.getTown().getTeam()) :
                                        "~" :
                                "Disabled"
                )))
                .sendTo(sender);

        return CommandResult.success();
    }

}
