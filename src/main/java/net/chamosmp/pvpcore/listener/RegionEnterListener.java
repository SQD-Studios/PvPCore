package net.chamosmp.pvpcore.listener;

import net.chamosmp.pvpcore.PvpcorePlugin;
import net.chamosmp.pvpcore.manager.RegionBlockManager;
import net.chamosmp.sqdlib.paper.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class RegionEnterListener implements Listener {
    private final RegionBlockManager regionBlockManager;
    private final PvpcorePlugin plugin;

    public RegionEnterListener(RegionBlockManager regionBlockManager, PvpcorePlugin plugin) {
        this.regionBlockManager = regionBlockManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        if (!RegionBlockManager.canAccessWe()) return;

        Player player = event.getPlayer();
        if (regionBlockManager.shouldBlockPlayer(player, event.getTo())) {
            Component message = ColorUtil.parse(
                    player,
                    plugin.getConfig().getString("safe-zones.block-message", "")
            );
            player.sendMessage(message);
            event.setCancelled(true);
        }
    }
}
