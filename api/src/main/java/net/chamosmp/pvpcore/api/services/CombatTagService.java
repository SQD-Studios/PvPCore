package net.chamosmp.pvpcore.api.services;

import net.chamosmp.pvpcore.api.model.TaggedPlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * The service related to the Combat Tag feature
 */
@SuppressWarnings("unused")
public interface CombatTagService {
    /**
     * Is CombatTag enabled?
     *
     * @return {@code true} if it is enabled {@code false} otherwise
     */
    boolean isCombatTagEnabled();

    /**
     * Tries to handle the combat between 2 players. It may fail if CombatTag is disabled by the user
     *
     * @param player1 The first player
     * @param player2 The second player
     * @apiNote If you want both players to get in combat correctly you should call it twice, and the second time switching the parameters
     */
    void handleCombat(@NonNull Player player1, @NonNull Player player2);

    /**
     * Removes a player from combat
     *
     * @param player the player to remove from combat
     */
    void removeFromCombat(@NonNull Player player);

    /**
     * Is this player in combat?
     *
     * @param player the player
     * @return is the player in combat
     */
    boolean isInCombat(@NonNull Player player);

    /**
     * Gets the {@link TaggedPlayer} from a player
     *
     * @param player the player to get it from
     * @return the {@link TaggedPlayer}
     */
    TaggedPlayer getTaggedPlayer(@NonNull Player player);
}
