package net.chamosmp.pvpcore.api.event.combat;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

/**
 * This {@link Event} is fired before it tries to handle the combat of the players, after identifying that both attacker and defender are players from the {@link EntityDamageByEntityEvent}
 */
public class PlayerWentIntoCombatEvent extends PlayerEvent implements Cancellable {
    private boolean cancelled;
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player2;

    @ApiStatus.Internal
    public PlayerWentIntoCombatEvent(@NonNull Player player1, @NonNull Player player2) {
        super(player1);

        this.player2 = player2;
    }

    /**
     * Gets the 2nd player associated with this event
     *
     * @return the player
     */
    public Player getPlayer2() {
        return player2;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}