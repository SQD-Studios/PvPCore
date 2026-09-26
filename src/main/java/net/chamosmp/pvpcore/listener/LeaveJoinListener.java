package net.chamosmp.pvpcore.listener;

import net.chamosmp.pvpcore.PvpcorePlugin;
import net.chamosmp.pvpcore.manager.CombatTagManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveJoinListener implements Listener {

    private final CombatTagManager tagManager;
    private final PvpcorePlugin plugin;

    public LeaveJoinListener(CombatTagManager tagManager, PvpcorePlugin plugin) {
        this.tagManager = tagManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (event.getReason().equals(PlayerQuitEvent.QuitReason.DISCONNECTED) && tagManager.isInCombat(event.getPlayer()) &&
                plugin.getConfig().getBoolean("combat-tag.kill-on-quit", true)) {
            event.getPlayer().kill();
        }
    }
}