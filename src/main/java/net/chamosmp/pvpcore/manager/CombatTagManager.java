package net.chamosmp.pvpcore.manager;

import io.papermc.paper.util.Tick;
import net.chamosmp.pvpcore.PvpcorePlugin;
import net.chamosmp.pvpcore.model.TaggedPlayer;
import net.chamosmp.sqdlib.paper.util.ColorUtil;
import net.chamosmp.sqdlib.paper.util.SchedulerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatTagManager {
    private final PvpcorePlugin plugin;

    private final Map<UUID, TaggedPlayer> tags = new ConcurrentHashMap<>();

    public CombatTagManager(PvpcorePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isCombatTagEnabled() {
        return plugin.getConfig().getBoolean("combat-tag.enabled", false);
    }

    public void handleCombat(Player player1, Player player2) {
        String message = plugin.getConfig().getString("combat-tag.combat-tagged-message");

        TaggedPlayer player1Tagged = tags.get(player1.getUniqueId());
        if (player1Tagged != null) {
            // If the player is in combat and the "player2" isn't in combat with him
            // add "player2" to the list of the people he's in combat with
            // and increases the time of his combat timer to the value set in the config
            if (!player1Tagged.inCombatWith().contains(player2)) {
                tags.put(player1.getUniqueId(), player1Tagged.addMorePeopleInCombat(player2).increaseInCombat(plugin));
            } else {
                tags.put(player1.getUniqueId(), player1Tagged.increaseInCombat(plugin));
            }
        } else {
            // This happens if player1 isn't "renewing" his combat and instead is entirely new.
            // It tries to send a message saying that he's in combat, adds him to the tagged players
            // sends the action bar and then schedules the next update in 1 second
            if (message != null)
                player1.sendMessage(ColorUtil.parse(player1, message, Map.of("otherPlayer", player2.getName())));
            TaggedPlayer newPlayer1Tagged = new TaggedPlayer(
                    player1,
                    new ArrayList<>(List.of(player2)),
                    plugin.getConfig().getInt("combat-tag.combat-duration", 20)
            );
            newPlayer1Tagged.sendActionBar(plugin);
            tags.put(player1.getUniqueId(), newPlayer1Tagged);
            scheduleUpdate(player1);
        }
    }

    public void scheduleUpdate(Player player) {
        SchedulerUtil.runDelayed(plugin, () -> {
            TaggedPlayer taggedPlayer = tags.get(player.getUniqueId());

            // If the player is removed from other ways (e.g., the players he was in combat with died)
            // it should have been null
            if (taggedPlayer == null) return;

            // If they're in combat for 0 seconds, it should remove them from the tagged people
            // and not reschedule nor send the action bar again
            if (taggedPlayer.inCombatFor() == 0) {
                tags.remove(taggedPlayer.player().getUniqueId());
                return;
            }

            // Creating and putting a new tagged player, with the same values except the
            // time the player was in combat, which will be decreased by 1 and then
            // send the action bar
            TaggedPlayer newTaggedPlayer = taggedPlayer.decreaseInCombat();
            tags.put(taggedPlayer.player().getUniqueId(), newTaggedPlayer);
            newTaggedPlayer.sendActionBar(plugin);

            scheduleUpdate(player); // Reschedule the task for the player
        }, Tick.tick().fromDuration(Duration.ofSeconds(1)));
    }

    public boolean isInCombat(Player player) {
        return tags.containsKey(player.getUniqueId());
    }

    public void onPlayerQuit(PlayerQuitEvent event) {
        TaggedPlayer taggedPlayer = tags.get(event.getPlayer().getUniqueId());
        if (taggedPlayer != null) {
            tags.remove(taggedPlayer.player().getUniqueId());
            for (Player player : taggedPlayer.inCombatWith()) {
                TaggedPlayer newTaggedPlayer = tags.get(player.getUniqueId());
                if (newTaggedPlayer != null) {
                    tags.put(newTaggedPlayer.player().getUniqueId(), newTaggedPlayer.removePeopleFromCombat(event.getPlayer(), plugin));
                }
            }
        }
    }
}
