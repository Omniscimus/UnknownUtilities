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
 * Executed by a player to invite another player to come over.
 */
public class TeleportAskHereCommand extends Command {

    private final TeleportRequestManager manager;

    /**
     * A list of aliases of this command
     */
    private static final String[] aliases = {
	"tpahere"
    };

    /**
     * Constructs a new TeleportCommand.
     *
     * @param manager
     */
    public TeleportAskHereCommand(TeleportRequestManager manager) {
	super(aliases);
	this.manager = manager;
    }

    @Override
    protected CommandCallable constructCallable() {
	return CommandSpec.builder()
		.description(Text.of("Asks another player if they would like to teleport to you."))
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
	Player targetPlayer = (Player) src;

	Object playerToTeleportThing = args.getOne("player")
		.orElseThrow(this::getSyntaxException);
	Player playerToTeleport = (Player) playerToTeleportThing;

	// targetPlayer asks playerToTeleport: 'Would you like to teleport to me?'
	try {
	    manager.registerTeleportRequest(new TeleportRequest(targetPlayer, playerToTeleport, targetPlayer));

	    playerToTeleport.sendMessage(Text.builder()
		    .append(Text.builder(targetPlayer.getName()).style(TextStyles.ITALIC).build())
		    .append(Text.builder(" would like me to teleport to them.")
			    .append(Text.NEW_LINE)
			    .append(Text.of("Type "))
			    .build())
		    .append(Text.builder("/tpyes").style(TextStyles.BOLD).build())
		    .append(Text.of(" to go there."))
		    .color(TextColors.GOLD).build());

	    targetPlayer.sendMessage(Text.builder("Hey ")
		    .append(Text.builder(playerToTeleport.getName()).style(TextStyles.ITALIC).build())
		    .append(Text.of(", would you like to teleport to me?"))
		    .color(TextColors.GOLD).build());

	    return CommandResult.success();
	} catch (LocationNotSafeException e) {
	    src.sendMessage(
		    Text.builder("I don't think it's safe for ")
		    .append(Text.builder(playerToTeleport.getName()).style(TextStyles.ITALIC).build())
		    .append(Text.of(" to teleport to me right now..."))
		    .color(TextColors.RED).build()
	    );
	    return CommandResult.empty();
	}
    }

}
