package net.chamosmp.pvpcore.api.event.region;

import net.chamosmp.pvpcore.api.model.PvpRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jspecify.annotations.NonNull;

/**
 * This event is fired after when, the player's is heading towards a region (From {@link PlayerMoveEvent#getTo()}, and is currently in combat and thus blocking him from entering that are
 */
public class PlayerPostBlockedFromEnteringRegion extends PlayerRegionEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public PlayerPostBlockedFromEnteringRegion(@NonNull Player player, @NonNull PvpRegion region) {
        super(player, region);
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}