package io.github.eufranio.spongytowns.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.eufranio.spongytowns.commands.bank.WithdrawCommand;
import io.github.eufranio.spongytowns.commands.resident.InfoCommand;
import io.github.eufranio.spongytowns.permission.Permissions;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;
import java.util.Map;

/**
 * Created by Frani on 13/04/2018.
 */
public class ResidentCommands {

    public static Map<String, CommandSpec> commands = Maps.newHashMap();

    public static CommandSpec registerCommands() {

        CommandSpec info = CommandSpec.builder()
                .description(Text.of("Shows info about you or a specific Resident"))
                .permission(Permissions.RESIDENT_INFO)
                .arguments(
                        GenericArguments.optional(
                                Arguments.resident(Text.of("resident"))
                        )
                )
                .executor(new InfoCommand())
                .build();
        commands.put("/town res info ", info);

        return CommandSpec.builder()
                .permission(Permissions.MAIN_COMMAND)
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

                    PaginationList.builder()
                            .contents(text)
                            .padding(Text.of(
                                    TextStyles.RESET, TextColors.DARK_GRAY, TextStyles.STRIKETHROUGH, "-"
                            ))
                            .title(Text.of(
                                    TextColors.GREEN, "Resident Commands"
                            ))
                            .header(Text.of(
                                    "Hover over the commands to see their description"
                            ))
                            .sendTo(sender);
                    return CommandResult.success();
                })
                .child(info, "info")
                .build();
    }

}
