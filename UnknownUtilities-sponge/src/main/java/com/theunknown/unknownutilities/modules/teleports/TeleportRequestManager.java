package com.theunknown.unknownutilities.modules.teleports;

import com.theunknown.unknownutilities.UnknownUtilitiesPlugin;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Registrar for all TeleportRequests.
 */
public class TeleportRequestManager {

    /**
     * Time in seconds before a teleport request times out.
     */
    private static final int REQUEST_TIMEOUT = 120;

    /**
     * List of current teleport requests
     */
    private final ArrayList<TeleportRequest> teleportRequests = new ArrayList<>();

    /**
     * Adds an asked teleport to the list. This request will be canceled if it
     * has not been accepted after a certain time.
     *
     * @param request the TeleportRequest containing the information about this
     * teleport
     */
    public void registerTeleportRequest(TeleportRequest request) {
	UnknownUtilitiesPlugin.plugin.getGame().getScheduler()
		.createTaskBuilder()
		.delay(REQUEST_TIMEOUT, TimeUnit.SECONDS)
		.execute(() -> {
		    this.deregisterTeleportRequest(request);
		})
		.submit(UnknownUtilitiesPlugin.plugin);
	teleportRequests.add(request);
    }

    /**
     * Removes an asked teleport from the list.
     *
     * @param request the teleport request to deregister
     * @return true if the specified request was successfully removed; false if
     * the request wasn't registered
     */
    public boolean deregisterTeleportRequest(TeleportRequest request) {
	return teleportRequests.remove(request);
    }

    /**
     * Accepts a teleport request.
     *
     * @param request the request to complete
     * @return whether the request was completed successfully
     */
    public boolean completeTeleportRequest(TeleportRequest request) {
	deregisterTeleportRequest(request);
	return request.accept();
    }

    /**
     * Gets whether the specified teleport request is already registered.
     *
     * @param request
     * @return
     */
    public boolean requestIsRegistered(TeleportRequest request) {
	return teleportRequests.contains(request);
    }

    /**
     * Gets a teleport request by the player who should approve of the request.
     *
     * @param asked
     * @return
     */
    public Optional<TeleportRequest> getRequestByAsked(Player asked) {
	for (TeleportRequest request : teleportRequests) {
	    if (request.getPlayerToAsk() == asked) {
		return Optional.of(request);
	    }
	}
	return Optional.empty();
    }

    /**
     * Gets a teleport request by the player to whom a player should be
     * teleported.
     *
     * @param target
     * @return
     */
    public Optional<TeleportRequest> getRequestByTarget(Player target) {
	for (TeleportRequest request : teleportRequests) {
	    if (request.getTargetPlayer() == target) {
		return Optional.of(request);
	    }
	}
	return Optional.empty();
    }

}
