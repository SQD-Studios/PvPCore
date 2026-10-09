package net.chamosmp.pvpcore.api.event.combat;

import net.chamosmp.pvpcore.api.model.TaggedPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

/**
 * This {@link Event} is fired after the processing that went to remove the player from combat (Removing him from Combat Map, sending out of combat message, etc.)
 */
public class PlayerWentOutOfCombat extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final TaggedPlayer taggedPlayer;

    @ApiStatus.Internal
    public PlayerWentOutOfCombat(@NotNull Player player, TaggedPlayer taggedPlayer) {
        super(player);
        this.taggedPlayer = taggedPlayer;
    }

    /**
     * Gets the old {@link TaggedPlayer} of this player
     *
     * @return the tagged player
     */
    public TaggedPlayer getTaggedPlayer() {
        return taggedPlayer;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
