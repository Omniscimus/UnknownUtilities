package com.theunknown.unknownutilities.modules.teleports;

import com.theunknown.unknownutilities.Command;
import com.theunknown.unknownutilities.exception.LocationNotSafeException;
import com.theunknown.unknownutilities.exception.SenderNotPlayerException;
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
import org.spongepowered.api.text.format.TextStyles;

/**
 * Executed by a player to invite another player for a teleport to them.
 */
public class TeleportAskCommand extends Command {

    private final TeleportRequestManager manager;

    /**
     * A list of aliases of this command
     */
    private static final String[] aliases = {
	"tpa", "tpask", "call"
    };

    /**
     * Constructs a new TeleportCommand.
     *
     * @param manager
     */
    public TeleportAskCommand(TeleportRequestManager manager) {
	super(aliases);
	this.manager = manager;
    }

    @Override
    protected CommandCallable constructCallable() {
	return CommandSpec.builder()
		.description(Text.of("Asks another player if you can teleport to them."))
		.permission(Command.PERMISSION_BASE + "tpa")
		.executor(this)
		.arguments(GenericArguments.player(Text.of("player")))
		.build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

	if (!(src instanceof Player)) {
	    throw new SenderNotPlayerException();
	}
	Player playerToTeleport = (Player) src;

	Object targetPlayerThing = args.getOne("player")
		.orElseThrow(this::getSyntaxException);
	Player targetPlayer = (Player) targetPlayerThing;

	// playerToTeleport asks targetPlayer: 'Can I teleport to you?'
	try {
	    TeleportRequest request = new TeleportRequest(targetPlayer, playerToTeleport, targetPlayer);
	    if (manager.requestIsRegistered(request)) {
		playerToTeleport.sendMessage(Text.builder("I don't think I have to ask that again.")
			.color(TextColors.RED).build());
		return CommandResult.empty();
	    }
	    manager.registerTeleportRequest(request);

	    targetPlayer.sendMessage(Text.builder()
		    .append(Text.builder(playerToTeleport.getName()).style(TextStyles.ITALIC).build())
		    .append(Text.builder(" would like to teleport to me.")
			    .append(Text.NEW_LINE)
			    .append(Text.of("Type "))
			    .build())
		    .append(Text.builder("/tpyes").style(TextStyles.BOLD).build())
		    .append(Text.of(" to let them teleport."))
		    .color(TextColors.GOLD).build());

	    playerToTeleport.sendMessage(Text.builder("Hey ")
		    .append(Text.builder(targetPlayer.getName()).style(TextStyles.ITALIC).build())
		    .append(Text.of(", I'd like to teleport to you."))
		    .color(TextColors.GOLD).build());

	    return CommandResult.success();

	} catch (LocationNotSafeException e) {
	    src.sendMessage(
		    Text.builder("I don't think it's safe to teleport to ")
		    .append(Text.builder(targetPlayer.getName()).style(TextStyles.ITALIC).build())
		    .append(Text.of(" right now..."))
		    .color(TextColors.RED).build()
	    );
	    return CommandResult.empty();
	}
    }

}
