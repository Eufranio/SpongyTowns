package io.github.eufranio.spongytowns.commands.town;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

/**
 * Created by Frani on 08/03/2018.
 */
public class ShowCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource sender, CommandContext context) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException(Text.of("This command can only be used by players!"));
        }
        return CommandResult.success();
    }
}
