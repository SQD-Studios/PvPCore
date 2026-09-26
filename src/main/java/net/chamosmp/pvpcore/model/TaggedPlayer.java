package net.chamosmp.pvpcore.model;

import net.chamosmp.sqdlib.paper.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

public record TaggedPlayer(
        Player player,
        List<Player> inCombatWith,
        int inCombatFor
) {
    public void sendActionBar(Plugin plugin) {
        String message = plugin.getConfig().getString("combat-tag.in-combat-action-bar");
        if (message == null || message.isEmpty()) return;
        player.sendActionBar(ColorUtil.parse(player, message, Map.of("time_left", inCombatFor)));
    }

    public TaggedPlayer decreaseInCombat() {
        return new TaggedPlayer(player, inCombatWith, inCombatFor - 1);
    }

    public TaggedPlayer addMorePeopleInCombat(Player player) {
        List<Player> inCombatWith = this.inCombatWith;
        inCombatWith.add(player);
        return new TaggedPlayer(this.player, inCombatWith, inCombatFor);
    }

    public TaggedPlayer removePeopleFromCombat(Player player, Plugin plugin) {
        List<Player> inCombatWith = this.inCombatWith;
        inCombatWith.remove(player);

        if (plugin.getConfig().getBoolean("combat-tag.end-combat-when-opponent-gone") && inCombatWith.isEmpty()) {
            return new TaggedPlayer(this.player, inCombatWith, 0);
        }

        return new TaggedPlayer(this.player, inCombatWith, inCombatFor);
    }

    public TaggedPlayer increaseInCombat(int amount, Plugin plugin) {
        TaggedPlayer taggedPlayer = new TaggedPlayer(player, inCombatWith, inCombatFor + amount);
        taggedPlayer.sendActionBar(plugin);
        return taggedPlayer;
    }

    public TaggedPlayer increaseInCombat(Plugin plugin) {
        return increaseInCombat(plugin.getConfig().getInt("combat-tag.on-additional-combat"), plugin);
    }
}