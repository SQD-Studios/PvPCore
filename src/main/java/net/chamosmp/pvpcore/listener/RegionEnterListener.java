package net.chamosmp.pvpcore.listener;

import net.chamosmp.pvpcore.PvpcorePlugin;
import net.chamosmp.pvpcore.api.event.combat.PlayerWentOutOfCombat;
import net.chamosmp.pvpcore.api.event.region.PlayerEnterRegionEvent;
import net.chamosmp.pvpcore.api.event.region.PlayerPostBlockedFromEnteringRegion;
import net.chamosmp.pvpcore.api.model.PvpRegion;
import net.chamosmp.pvpcore.manager.CombatTagManager;
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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegionEnterListener implements Listener {
    private final RegionBlockManager regionBlockManager;
    private final PvpcorePlugin plugin;
    private final CombatTagManager combatTagManager;

    public RegionEnterListener(RegionBlockManager regionBlockManager, PvpcorePlugin plugin, CombatTagManager combatTagManager) {
        this.regionBlockManager = regionBlockManager;
        this.plugin = plugin;
        this.combatTagManager = combatTagManager;
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        if (!RegionBlockManager.canAccessWe()) return;

        Player player = event.getPlayer();


        updateBarriers(player, event.getTo());

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

            PlayerPostBlockedFromEnteringRegion e = new PlayerPostBlockedFromEnteringRegion(player, playerRegion.getSecond());
            Bukkit.getPluginManager().callEvent(e);
        }
    }

    private void pushBack(Player player, Location from, Location to) {
        double strength = plugin.getConfig().getDouble("safe-zones.knockback", 1.0);

        double directionX = from.getX() - to.getX();
        double directionZ = from.getZ() - to.getZ();

        if (directionX == 0.0 && directionZ == 0.0) {
            Vector look = player.getLocation().getDirection().multiply(-1);
            directionX = look.getX();
            directionZ = look.getZ();
        }

        player.knockback(strength, directionX, directionZ);
    }

    @EventHandler
    public void playerCombatExpire(PlayerWentOutOfCombat event) {
        updateBarriers(event.getPlayer(), event.getPlayer().getLocation());
    }

    private static final Map<Player, List<Location>> activeBarriers = new ConcurrentHashMap<>();

    private void updateBarriers(Player player, Location to) {
        if (!combatTagManager.isInCombat(player)) {
            clearBarriers(player);
            return;
        }

        PvpRegion region = regionBlockManager.isPlayerInRegion(to).getSecond();
        if (region == null) return;

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("safe-zones.block-change");
        if (section == null) return;
        if (!section.getBoolean("enabled")) return;

        int radius = section.getInt("radius", 10);
        Material material = Material.getMaterial(section.getString("material", "").toUpperCase());

        if (material == null) return;

        Location pLoc = player.getLocation();

        if (!(region.distanceSquared(pLoc) > 25.0)) {
            List<Location> set = region.getBorderBlocks(pLoc, radius);
            activeBarriers.put(player, set);
            for (Location location : set) {
                player.sendBlockChange(location, material.createBlockData());
            }
        } else {
            clearBarriers(player);
        }
    }

    public void clearBarriers(Player player) {
        List<Location> oldBarriers = activeBarriers.remove(player);
        if (oldBarriers != null) {
            for (Location loc : oldBarriers) {
                player.sendBlockChange(loc, loc.getBlock().getBlockData());
            }
        }
    }
}
