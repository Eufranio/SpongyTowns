package io.github.eufranio.spongytowns.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.eufranio.spongytowns.BaseCommands;

import io.github.eufranio.spongytowns.commands.bank.DepositCommand;
import io.github.eufranio.spongytowns.commands.bank.InfoCommand;
import io.github.eufranio.spongytowns.commands.bank.WithdrawCommand;
import io.github.eufranio.spongytowns.permission.Permissions;
import io.github.eufranio.spongytowns.util.InfoBuilder;
import org.spongepowered.api.command.CommandMapping;
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
import java.util.Optional;

/**
 * Created by Frani on 14/03/2018.
 */
public class BankCommands {

    public static Map<String, CommandSpec> commands = Maps.newHashMap();

    public static CommandSpec registerCommands() {
        CommandSpec info = CommandSpec.builder()
                .description(Text.of("Shows info about the bank of the town or plot that you're in"))
                .permission(Permissions.INFO)
                .arguments(
                        GenericArguments.string(Text.of("town/plot")),
                        GenericArguments.optional(
                                Arguments.claim(Text.of("claim"))
                        )
                )
                .executor(new InfoCommand())
                .build();
        commands.put("/town bank info ", info);

        CommandSpec deposit = CommandSpec.builder()
                .description(Text.of("Deposits money from your account on your town/plot"))
                .permission(Permissions.DEPOSIT)
                .arguments(
                        GenericArguments.integer(Text.of("amount")),
                        GenericArguments.optional(
                                Arguments.claim(Text.of("claim"))
                        )
                )
                .executor(new DepositCommand())
                .build();
        commands.put("/town bank deposit ", deposit);

        CommandSpec withdraw = CommandSpec.builder()
                .description(Text.of("Withdraws money from the bank of your town/plot"))
                .permission(Permissions.WITHDRAW)
                .arguments(
                        GenericArguments.integer(Text.of("amount")),
                        GenericArguments.optional(
                                Arguments.claim(Text.of("claim"))
                        )
                )
                .executor(new WithdrawCommand())
                .build();
        commands.put("/town bank withdraw ", withdraw);

        return CommandSpec.builder()
                .permission(Permissions.MAIN_COMMAND)
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

                    InfoBuilder.create()
                            .title(Text.of(TextColors.GREEN, "Bank Commands"))
                            .header(Text.of("Hover over the commands to see their description"))
                            .add(text)
                            .sendTo(sender);

                    return CommandResult.success();
                })
                .child(info, "info")
                .child(deposit, "deposit")
                .child(withdraw, "withdraw")
                .build();
    }

}
