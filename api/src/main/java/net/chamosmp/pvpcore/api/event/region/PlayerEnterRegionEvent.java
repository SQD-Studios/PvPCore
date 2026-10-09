package net.chamosmp.pvpcore.api.event.region;

import net.chamosmp.pvpcore.api.model.PvpRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * This usually happens after the {@link PlayerMoveEvent} has fired, and the player from that event has entered a {@link PvpRegion}
 */
@SuppressWarnings("unused")
public class PlayerEnterRegionEvent extends PlayerRegionEvent implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final PlayerMoveEvent event;

    @ApiStatus.Internal
    public PlayerEnterRegionEvent(PvpRegion region, Player player, PlayerMoveEvent event) {
        super(player, region);

        this.event = event;
    }

    @Override
    public boolean isCancelled() {
        return event.isCancelled();
    }

    @Override
    public void setCancelled(boolean b) {
        event.setCancelled(b);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
