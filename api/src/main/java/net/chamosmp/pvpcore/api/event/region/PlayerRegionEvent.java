package net.chamosmp.pvpcore.api.event.region;

import net.chamosmp.pvpcore.api.model.PvpRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

@ApiStatus.Internal
public abstract class PlayerRegionEvent extends PlayerEvent {
    private final PvpRegion region;

    protected PlayerRegionEvent(@NonNull Player player, @NonNull PvpRegion region) {
        super(player);

        this.region = region;
    }

    /**
     * Get the {@link PvpRegion} related to this event
     *
     * @return the pvp region
     */
    public @NonNull PvpRegion getPvpRegion() {
        return region;
    }

    @Override
    public abstract @NonNull HandlerList getHandlers();
}