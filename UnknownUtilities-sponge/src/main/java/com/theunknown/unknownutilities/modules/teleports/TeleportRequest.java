package com.theunknown.unknownutilities.modules.teleports;

import com.theunknown.unknownutilities.UnknownUtilitiesPlugin;
import com.theunknown.unknownutilities.exception.LocationNotSafeException;
import java.util.Objects;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Represents a request from a player to be teleported to another player.
 */
public class TeleportRequest {

    /**
     * The player who should be asked whether this request should be fulfilled
     */
    private final Player playerToAsk;

    /**
     * The player who should be teleported to locationToTeleportTo if the
     * request is accepted
     */
    private final Player playerToTeleport;

    /**
     * The player to whom playerToTeleport will be teleported if playerToAsk
     * accepts this request
     */
    private final Player playerToTeleportTo;

    /**
     * The location to where playerToTeleport will be teleported if the request
     * is accepted by playerToAsk
     */
    private final Location<World> targetLocation;

    /**
     * @param playerToAsk the player whom should be asked if playerToTeleport
     * can teleport to playerToTeleportTo
     * @param playerToTeleport the player who should be teleported to
     * playerToTeleportTo if playerToAsk accepts this request
     * @param playerToTeleportTo the player to whom PlayerToTeleport should be
     * teleported if playerToAsk accepts this request
     * @throws LocationNotSafeException if playerToTeleportTo's location is not
     * safe to teleport to
     */
    public TeleportRequest(Player playerToAsk, Player playerToTeleport, Player playerToTeleportTo)
	    throws LocationNotSafeException {

	this.playerToAsk = playerToAsk;

	this.playerToTeleportTo = playerToTeleportTo;
	this.targetLocation = playerToTeleportTo.getLocation();
	if (!UnknownUtilitiesPlugin.plugin.getGame()
		.getTeleportHelper().getSafeLocation(targetLocation).isPresent()) {
	    throw new LocationNotSafeException();
	}

	this.playerToTeleport = playerToTeleport;
    }

    /**
     * Executes the teleportation.
     *
     * @see TeleportRequestManager#completeTeleportRequest(TeleportRequest)
     *
     * @return true if the player was teleported successfully; false if the
     * player wasn't teleported because the target location wasn't safe
     */
    protected boolean accept() {

	boolean success = playerToTeleport.setLocationSafely(targetLocation);
	if (success) {
	    playerToTeleport.sendMessage(Text.builder("Poof! I just teleported to ")
		    .append(Text.builder(playerToTeleportTo.getName()).style(TextStyles.ITALIC).build())
		    .append(Text.of("."))
		    .color(TextColors.GOLD).build());
	    playerToTeleportTo.sendMessage(Text.builder()
		    .append(Text.builder(playerToTeleport.getName()).style(TextStyles.ITALIC).build())
		    .append(Text.of(" just teleported to me."))
		    .color(TextColors.GOLD).build());
	} else {
	    playerToTeleport.sendMessage(Text.builder("On second thoughts, I think it's not really safe to teleport to ")
		    .append(Text.builder(playerToTeleportTo.getName()).style(TextStyles.ITALIC).build())
		    .append(Text.of("..."))
		    .color(TextColors.RED).build());
	}

	return success;
    }

    /**
     * Gets the player who should approve of this teleport request.
     *
     * @return
     */
    public Player getPlayerToAsk() {
	return playerToAsk;
    }

    public Player getTargetPlayer() {
	return playerToTeleportTo;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof TeleportRequest)) {
	    return false;
	}
	final TeleportRequest otherRequest = (TeleportRequest) obj;
	return (playerToAsk.equals(otherRequest.playerToAsk)
		&& playerToTeleport.equals(otherRequest.playerToTeleport)
		&& playerToTeleportTo.equals(otherRequest.playerToTeleportTo));
    }

    @Override
    public int hashCode() {
	int hash = 3;
	hash = 79 * hash + Objects.hashCode(this.playerToAsk);
	hash = 79 * hash + Objects.hashCode(this.playerToTeleport);
	hash = 79 * hash + Objects.hashCode(this.playerToTeleportTo);
	return hash;
    }

}
