package net.chamosmp.pvpcore.manager;

import net.chamosmp.pvpcore.WorldEditIntegration;
import net.chamosmp.pvpcore.model.PvpRegion;
import net.chamosmp.sqdlib.paper.util.LoggerUtil;
import net.chamosmp.sqdlib.util.log.LogType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RegionBlockManager {
    private final CombatTagManager combatTagManager;

    private static final List<PvpRegion> regions = new ArrayList<>();

    private final @Nullable WorldEditIntegration worldEditIntegration;

    public RegionBlockManager(CombatTagManager combatTagManager) {
        this.combatTagManager = combatTagManager;

        if (canAccessWe()) {
            worldEditIntegration = new WorldEditIntegration();
        } else {
            worldEditIntegration = null;
        }
    }

    public @Nullable WorldEditIntegration getWorldEditIntegration() {
        return worldEditIntegration;
    }

    public static void reloadRegionsFromConfig(Plugin plugin) {
        if (!canAccessWe()) return;

        regions.clear();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("safe-zones.regions");
        if (section == null) return;

        List<PvpRegion> regions = new ArrayList<>();
        section.getKeys(false).forEach(key -> regions.add(section.getObject(key, PvpRegion.class)));

        RegionBlockManager.regions.addAll(regions);
    }

    private boolean isPlayerInRegion(Location player) {
        for (PvpRegion region : regions) {
            if (region == null) {
                LoggerUtil.log(LogType.WARNING, "Some of the regions in the config are null!");
                continue;
            }
            if (region.contains(player)) return true;
        }
        return false;
    }

    public boolean shouldBlockPlayer(Player player, Location location) {
        return isPlayerInRegion(location) && combatTagManager.isInCombat(player);
    }

    public static boolean canAccessWe() {
        try {
            Class.forName("com.sk89q.worldedit.IncompleteRegionException");
            return Bukkit.getPluginManager().isPluginEnabled("WorldEdit");
        } catch (Exception e) {
            return false;
        }
    }
}
