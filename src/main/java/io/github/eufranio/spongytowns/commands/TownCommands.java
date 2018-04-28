package io.github.eufranio.spongytowns.commands;

import com.google.common.collect.Lists;
import io.github.eufranio.spongytowns.BaseCommands;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.commands.common.ClaimCommand;
import io.github.eufranio.spongytowns.commands.common.DeleteCommand;
import io.github.eufranio.spongytowns.commands.common.InfoCommand;
import io.github.eufranio.spongytowns.commands.common.UnclaimCommand;
import io.github.eufranio.spongytowns.commands.town.*;
import io.github.eufranio.spongytowns.permission.Permissions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;

/**
 * Created by Frani on 27/01/2018.
 */
public class TownCommands extends BaseCommands {

    public static void registerCommands(SpongyTowns plugin) {
        CommandSpec create = CommandSpec.builder()
                .description(Text.of("Creates a new town at the chunk you're in"))
                .permission(Permissions.CREATE_TOWN)
                .arguments(
                        GenericArguments.text(Text.of("name"), TextSerializers.FORMATTING_CODE, false),
                        GenericArguments.optional(
                                GenericArguments.user(Text.of("player"))
                        )
                )
                .executor(new CreateCommand())
                .build();
        commands.put("/town create ", create);

        CommandSpec delete = CommandSpec.builder()
                .description(Text.of("Deletes the specified claim, including all subclaims"))
                .permission(Permissions.DELETE_TOWN)
                .arguments(
                        Arguments.claim(Text.of("claim")),
                        GenericArguments.optional(
                                GenericArguments.user(Text.of("player"))
                        )
                )
                .executor(new DeleteCommand())
                .build();
        commands.put("/town delete ", delete);

        CommandSpec claim = CommandSpec.builder()
                .description(Text.of("Claims the chunk you're in to the nearest town, or an outpost one if specified"))
                .permission(Permissions.CLAIM)
                .arguments(
                        GenericArguments.optional(
                                Arguments.claim(Text.of("claim"))
                        )
                )
                .executor(new ClaimCommand(false))
                .build();
        commands.put("/town claim ", claim);

        CommandSpec unclaim = CommandSpec.builder()
                .description(Text.of("Unclaims the chunk you're in"))
                .permission(Permissions.UNCLAIM)
                .arguments(
                        GenericArguments.optional(
                                Arguments.town(Text.of("town"))
                        )
                )
                .executor(new UnclaimCommand())
                .build();
        commands.put("/town unclaim ", unclaim);

        CommandSpec info = CommandSpec.builder()
                .description(Text.of("Shows info about the specified town (or your own one)"))
                .permission(Permissions.INFO)
                .arguments(GenericArguments.optional(
                        Arguments.claim(Text.of("claim"))
                ))
                .executor(new InfoCommand())
                .build();
        commands.put("/town info ", info);

        CommandSpec setOwner = CommandSpec.builder()
                .description(Text.of("Changes the owner of a specific claim"))
                .permission(Permissions.SET_OWNER)
                .arguments(
                        GenericArguments.uuid(Text.of("uuid")),
                        GenericArguments.optional(
                                Arguments.claim(Text.of("claim"))
                        )
                )
                .executor(new SetOwnerCommand())
                .build();
        commands.put("/town setOwner ", setOwner);

        CommandSpec invite = CommandSpec.builder()
                .description(Text.of("Invites a player to your town"))
                .permission(Permissions.INVITE)
                .arguments(
                        Arguments.resident(Text.of("resident")),
                        GenericArguments.optional(
                                Arguments.claim(Text.of("claim"))
                        )
                )
                .executor(new InviteCommand())
                .build();
        commands.put("/town invite ", invite);

        CommandSpec invites = CommandSpec.builder()
                .description(Text.of("Checks your in invites to towns/plots"))
                .permission(Permissions.VIEW_INVITES)
                .arguments(
                        GenericArguments.optional(
                                GenericArguments.requiringPermission(
                                        Arguments.resident(Text.of("resident")),
                                        Permissions.VIEW_INVITES_OTHER
                                )
                        )
                )
                .executor(new InvitesCommand())
                .build();
        commands.put("/town invites ", invites);

        CommandSpec accept = CommandSpec.builder()
                .description(Text.of("Accepts invites to other claims"))
                .permission(Permissions.ACCEPT)
                .arguments(
                        GenericArguments.optional(
                                GenericArguments.firstParsing(
                                        GenericArguments.uuid(Text.of("uuid")),
                                        Arguments.claim(Text.of("claim"))
                                )
                        )
                )
                .executor(new AcceptCommand())
                .build();
        commands.put("/town accept ", accept);

        CommandSpec deny = CommandSpec.builder()
                .description(Text.of("Denies invites to a claim"))
                .permission(Permissions.DENY)
                .arguments(
                        GenericArguments.optional(
                                GenericArguments.firstParsing(
                                        GenericArguments.uuid(Text.of("uuid")),
                                        Arguments.claim(Text.of("claim"))
                                )
                        )
                )
                .executor(new DenyCommand())
                .build();
        commands.put("/town deny ", deny);

        CommandSpec kick = CommandSpec.builder()
                .description(Text.of("Kicks a specific resident from your town"))
                .permission(Permissions.KICK)
                .arguments(
                        Arguments.resident(Text.of("resident")),
                        GenericArguments.optional(
                                GenericArguments.requiringPermission(
                                        Arguments.claim(Text.of("claim")),
                                        Permissions.KICK_ADMIN
                                )
                        )
                )
                .executor(new KickCommand())
                .build();
        commands.put("/town kick ", kick);

        CommandSpec town = CommandSpec.builder()
                .permission(Permissions.MAIN_COMMAND)
                .executor((sender, context) -> {
                    List<Text> text = Lists.newArrayList();

                    TownCommands.commands.forEach((name, spec) ->
                        text.add(Text.of(
                                TextColors.GRAY, "* ",
                                TextColors.YELLOW, name, spec.getUsage(sender))
                                .toBuilder()
                                .onHover(TextActions.showText(spec.getShortDescription(sender).get()))
                                .build())
                    );

                    // manually adding complex subcommands
                    text.add(Text.of(
                            TextColors.GRAY, "* ",
                            TextColors.YELLOW, "/town bank ...")
                            .toBuilder()
                            .onHover(TextActions.showText(Text.of("Main town bank related commands")))
                            .build());

                    text.add(Text.of(
                            TextColors.GRAY, "* ",
                            TextColors.YELLOW, "/town res ...")
                            .toBuilder()
                            .onHover(TextActions.showText(Text.of("Main resident related commands")))
                            .build());

                    PaginationList.builder()
                            .contents(text)
                            .padding(Text.of(
                                    TextStyles.RESET, TextColors.DARK_GRAY, TextStyles.STRIKETHROUGH, "-"
                            ))
                            .title(Text.of(
                                    TextColors.GREEN, "SpongyTowns Commands"
                            ))
                            .header(Text.of(
                                    "Hover over the commands to see their description"
                            ))
                            .sendTo(sender);
                    return CommandResult.success();
                })
                .child(create, "create", "new", "c")
                .child(delete, "delete", "d")
                .child(claim, "claim")
                .child(unclaim, "unclaim", "u")
                .child(info, "info")
                .child(setOwner, "setOwner")
                .child(invite, "invite")
                .child(invites, "invites")
                .child(accept, "accept")
                .child(deny, "deny")
                .child(kick, "kick")
                .child(BankCommands.registerCommands(), "bank", "b")
                .child(ResidentCommands.registerCommands(), "res", "resident", "r")
                .build();

        mapping = Sponge.getCommandManager().register(plugin, town, "town", "t", "city");
    }

}
