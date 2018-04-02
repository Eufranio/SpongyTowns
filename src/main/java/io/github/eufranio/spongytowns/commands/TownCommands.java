package io.github.eufranio.spongytowns.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.eufranio.spongytowns.BaseCommands;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.commands.town.*;
import io.github.eufranio.spongytowns.permission.Permissions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandMapping;
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
import java.util.Map;
import java.util.Optional;

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
                .description(Text.of("Deletes the town at the chunk you're in, including all plots and claims"))
                .permission(Permissions.DELETE_TOWN)
                .arguments(
                        Arguments.town(Text.of("town")),
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
                                Arguments.town(Text.of("town"))
                        )
                )
                .executor(new ClaimCommand())
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
                        Arguments.town(Text.of("town"))
                ))
                .executor(new InfoCommand())
                .build();
        commands.put("/town info ", info);

        CommandSpec town = CommandSpec.builder()
                .permission("spongytowns.command.town.main")
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
                .child(unclaim, "unclaim")
                .child(info, "info")
                .child(BankCommands.registerCommands(), "bank")
                .build();

        mapping = Sponge.getCommandManager().register(plugin, town, "town", "t", "city");
    }

}
