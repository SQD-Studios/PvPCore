package net.chamosmp.pvpcore.listener;

import net.chamosmp.pvpcore.PvpcorePlugin;
import net.chamosmp.pvpcore.api.event.region.PlayerEnterRegionEvent;
import net.chamosmp.pvpcore.api.event.region.PlayerPostBlockedFromEnteringRegion;
import net.chamosmp.pvpcore.api.model.PvpRegion;
import net.chamosmp.pvpcore.manager.RegionBlockManager;
import net.chamosmp.sqdlib.lang.value.DoubleValue;
import net.chamosmp.sqdlib.paper.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

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

        DoubleValue<Boolean, PvpRegion> isPlayerRegion = regionBlockManager.isPlayerInRegion(player.getLocation());
        if (isPlayerRegion.getFirst()) {
            PlayerEnterRegionEvent playerEnterRegionEvent = new PlayerEnterRegionEvent(isPlayerRegion.getSecond(), player, event);
            Bukkit.getPluginManager().callEvent(playerEnterRegionEvent);
        }

        DoubleValue<Boolean, PvpRegion> playerRegion = regionBlockManager.shouldBlockPlayer(player, event.getTo());
        if (playerRegion.getFirst()) {
            Component message = ColorUtil.parse(
                    player,
                    plugin.getConfig().getString("safe-zones.block-message", "")
            );

            player.sendMessage(message);
            event.setCancelled(true);

            pushBack(player, event.getTo(), event.getFrom());
            showBarriers(player, playerRegion.getSecond());

            PlayerPostBlockedFromEnteringRegion e = new PlayerPostBlockedFromEnteringRegion(player, playerRegion.getSecond());
            Bukkit.getPluginManager().callEvent(e);
        }
    }

    private void pushBack(Player player, Location from, Location to) {
        double distance = plugin.getConfig().getDouble("safe-zones.knockback", 50.0F);
        Vector direction = from.toVector().subtract(to.toVector());

        if (direction.lengthSquared() > (double) 0.0F) {
            direction = direction.normalize();
        } else {
            direction = player.getLocation().getDirection().multiply(-1).normalize();
        }

        direction.multiply(distance);
        direction.setY((double) 0.5F);
        player.setVelocity(direction);
    }

    private void showBarriers(Player player, PvpRegion region) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("safe-zones.block-change");
        if (section == null) return;
        if (!section.getBoolean("enabled")) return;

        int radius = section.getInt("radius", 10);
        Material material = Material.getMaterial(section.getString("material", "").toUpperCase());

        if (material == null) return;

        Location pLoc = player.getLocation();
        if (!(region.distanceSquared(pLoc) > (double) 25.0F)) {
            for (Location location : region.getBorderBlocks(pLoc, radius)) {
                player.sendBlockChange(location, material.createBlockData());
            }
        }
    }
}
