package net.chamosmp.pvpcore.api;

import net.chamosmp.pvpcore.api.services.CombatTagService;
import net.chamosmp.pvpcore.api.services.RegionBlockService;
import org.jspecify.annotations.NonNull;

/**
 * The api of PvpCore
 */
@SuppressWarnings("unused")
public interface PvpCoreApi {
    /**
     * Gets the {@link CombatTagService}
     *
     * @return the {@link CombatTagService}
     */
    @NonNull CombatTagService getCombatTagService();

    /**
     * Gets the {@link RegionBlockService}
     *
     * @return the {@link RegionBlockService}
     */
    @NonNull RegionBlockService getRegionBlockService();
}