package net.chamosmp.pvpcore.api.services;

import com.sk89q.worldedit.WorldEdit;
import net.chamosmp.pvpcore.api.model.PvpRegion;
import net.chamosmp.sqdlib.lang.value.DoubleValue;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Services related to the {@link PvpRegion}s and utilities to block players from entering them
 */
@SuppressWarnings("unused")
public interface RegionBlockService {
    /**
     * If it can access the {@link WorldEdit} classes, and is the plugin enabled
     *
     * @return can we access worldedit classes at runtime?
     */
    boolean canAccessWorldEdit();

    /**
     * This happens if the {@link Location} parameter, is inside a region, and the player is in combat,
     * which then it also returns the {@link PvpRegion} the location is in, {@code null} if the location isn't inside a region
     *
     * @param player   the player to check if is in combat
     * @param location the location to check if it is in a region
     * @return It returns a {@link DoubleValue}, which contains: 1. If the {@link Location} is in a region 2. The region the location is in, {@code null} if it isn't inside a region
     */
    DoubleValue<Boolean, @Nullable PvpRegion> shouldBlockPlayer(@NonNull Player player, @NonNull Location location);

    /**
     * Checks if a {@link Location} is inside a region
     *
     * @param playerLocation the {@link Location} to see if it is on a region
     * @return It returns a {@link DoubleValue}, which contains: 1. If the {@link Location} is in a region 2. The region the location is in, {@code null} if it isn't inside a region
     */
    DoubleValue<Boolean, @Nullable PvpRegion> isPlayerInRegion(@NonNull Location playerLocation);

    /**
     * Gets the {@link WorldEditService}, which uses the {@link WorldEdit} classes
     *
     * @return the {@link WorldEditService} if we can access WorldEdit classes, {@code null} otherwise
     */
    @Nullable WorldEditService getWorldEditIntegration();
}
