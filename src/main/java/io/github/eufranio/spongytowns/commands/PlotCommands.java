package io.github.eufranio.spongytowns.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.commands.common.ClaimCommand;
import io.github.eufranio.spongytowns.commands.common.DeleteCommand;
import io.github.eufranio.spongytowns.commands.common.InfoCommand;
import io.github.eufranio.spongytowns.commands.plot.CreateCommand;
import io.github.eufranio.spongytowns.permission.Permissions;
import io.github.eufranio.spongytowns.util.InfoBuilder;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;
import java.util.Map;

/**
 * Created by Frani on 16/04/2018.
 */
public class PlotCommands {

    public static Map<String, CommandSpec> commands = Maps.newHashMap();

    public static void registerCommands() {

        CommandSpec info = CommandSpec.builder()
                .description(Text.of("Shows info about the specified claim (or your own one)"))
                .permission(Permissions.INFO)
                .arguments(GenericArguments.optional(
                        Arguments.claim(Text.of("claim"))
                ))
                .executor(new InfoCommand())
                .build();
        commands.put("/plot info ", info);

        CommandSpec create = CommandSpec.builder()
                .description(Text.of("Creates a new plot on the chunk that you're in"))
                .permission(Permissions.CREATE_PLOT)
                .arguments(
                        GenericArguments.text(Text.of("name"), TextSerializers.FORMATTING_CODE, false),
                        GenericArguments.optional(
                                GenericArguments.user(Text.of("player"))
                        )
                )
                .executor(new CreateCommand())
                .build();
        commands.put("/plot create ", create);

        CommandSpec delete = CommandSpec.builder()
                .description(Text.of("Deletes the specified plot, including all subclaims"))
                .permission(Permissions.DELETE_PLOT)
                .arguments(
                        Arguments.claim(Text.of("claim"))
                )
                .executor(new DeleteCommand())
                .build();
        commands.put("/plot delete ", delete);

        CommandSpec claim = CommandSpec.builder()
                .description(Text.of("Claims a chunk as part of a plot"))
                .arguments(
                        GenericArguments.optional(
                                Arguments.claim(Text.of("claim"))
                        )
                )
                .executor(new ClaimCommand(true))
                .build();
        commands.put("/plot claim ", claim);

        CommandSpec main = CommandSpec.builder()
                .permission(Permissions.MAIN_COMMAND)
                .description(Text.of("Main plot commands/help"))
                .executor((sender, context) -> {
                    List<Text> text = Lists.newArrayList();
                    commands.forEach((name, spec) ->
                            text.add(Text.of(
                                    TextColors.GRAY, "* ",
                                    TextColors.YELLOW, name, spec.getUsage(sender))
                                    .toBuilder()
                                    .onHover(TextActions.showText(spec.getShortDescription(sender).get()))
                                    .build())
                    );

                    InfoBuilder.create()
                            .add(text)
                            .title("Plot Commands")
                            .header("Hover over the commands to see their description")
                            .sendTo(sender);
                    return CommandResult.success();
                })
                .child(info, "info")
                .child(create, "create")
                .child(claim, "claim")
                .child(delete, "delete")
                .build();
        Sponge.getCommandManager().register(SpongyTowns.getInstance(), main, "plot", "pl", "p");
    }
}
