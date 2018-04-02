package io.github.eufranio.spongytowns.commands;

import com.google.common.collect.Lists;
import io.github.eufranio.spongytowns.BaseCommands;

import io.github.eufranio.spongytowns.commands.bank.InfoCommand;
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

/**
 * Created by Frani on 14/03/2018.
 */
public class BankCommands extends BaseCommands {

    public static CommandSpec registerCommands() {
        CommandSpec info = CommandSpec.builder()
                .description(Text.of("Shows info about the bank of the town or plot that you're in"))
                .permission(Permissions.INFO)
                .arguments(GenericArguments.string(Text.of("town or plot")))
                .executor(new InfoCommand())
                .build();
        commands.put("/t bank info ", info);

        return CommandSpec.builder()
                .permission("spongytowns.command.town.main")
                .executor((sender, context) -> {
                    List<Text> text = Lists.newArrayList();

                    BankCommands.commands.forEach((name, spec) ->
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
                                    TextColors.GOLD, "Bank Commands"
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
