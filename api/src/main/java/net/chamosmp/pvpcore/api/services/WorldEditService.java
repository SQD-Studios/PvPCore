package net.chamosmp.pvpcore.api.services;

import com.sk89q.worldedit.regions.Region;
import net.chamosmp.pvpcore.api.model.PvpRegion;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * The Service which contains actions which need access to WorldEdit classes
 */
public interface WorldEditService {
    /**
     * Gets a {@link PvpRegion}, from the selected {@link Region} of the {@link Player}'s selection in WorldEdit
     *
     * @param player the player to get the selection from
     * @return the {@link PvpRegion} if something was selected, {@code null} otherwise
     */
    @Nullable PvpRegion getPvpRegion(@NonNull Player player);

    /**
     * Gets a {@link Region}, of the {@link Player}'s selection in WorldEdit
     *
     * @param player the player to get the selection from
     * @return the {@link Region} if something was selected, {@code null} otherwise
     */
    @Nullable Region getRegion(@NonNull Player player);
}
