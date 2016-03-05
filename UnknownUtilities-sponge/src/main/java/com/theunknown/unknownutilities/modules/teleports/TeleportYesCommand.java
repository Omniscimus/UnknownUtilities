package com.theunknown.unknownutilities.modules.teleports;

import com.theunknown.unknownutilities.Command;
import com.theunknown.unknownutilities.exception.SenderNotPlayerException;
import java.util.Optional;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Executed by a player to accept another player's teleport request.
 */
public class TeleportYesCommand extends Command {

    private final TeleportRequestManager manager;

    /**
     * A list of aliases of this command
     */
    private static final String[] aliases = {
	"tpyes", "tpaccept"
    };

    /**
     * Constructs a new TeleportCommand.
     *
     * @param manager
     */
    public TeleportYesCommand(TeleportRequestManager manager) {
	super(aliases);
	this.manager = manager;
    }

    @Override
    protected CommandCallable constructCallable() {
	return CommandSpec.builder()
		.description(Text.of("Accepts someone's teleport request."))
		.permission(PERMISSION_BASE + "tpa")
		.executor(this)
		.arguments(GenericArguments.optionalWeak(GenericArguments.player(Text.of("player"))))
		.build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

	if (!(src instanceof Player)) {
	    throw new SenderNotPlayerException();
	}
	Player sender = (Player) src;

	Optional<Player> targetPlayer = args.getOne("player");
	Optional<TeleportRequest> optRequest = targetPlayer.isPresent()
		? manager.getRequestByTarget(targetPlayer.get()) : manager.getRequestByAsked(sender);

	if (optRequest.isPresent()) {
	    manager.completeTeleportRequest(optRequest.get());
	    return CommandResult.builder().affectedEntities(1).successCount(1).build();
	} else {
	    sender.sendMessage(Text.builder("I don't remember any player to have asked me to teleport recently.")
		    .color(TextColors.RED).build());
	    return CommandResult.empty();
	}
    }

}
