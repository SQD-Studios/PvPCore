package net.chamosmp.pvpcore.listener;

import net.chamosmp.pvpcore.PvpcorePlugin;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ExplodeListener implements Listener {

    private final PvpcorePlugin plugin;

    public ExplodeListener(PvpcorePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (plugin.getConfig().getBoolean("explosive-disabler.crystals") && event.getEntity().getType() == EntityType.END_CRYSTAL) {
            event.setCancelled(true);
        }

        if (plugin.getConfig().getBoolean("explosive-disabler.minecarts")) {
            if (event.getEntity().getType() == EntityType.TNT_MINECART) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent event) {
        if (plugin.getConfig().getBoolean("explosive-disabler.crystals") && event.getExplodedBlockState().getType() == Material.RESPAWN_ANCHOR) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent event) {
        if (plugin.getConfig().getBoolean("explosive-disabler.crystals")) {
            if (event.getDamager().getType() == EntityType.END_CRYSTAL) {
                event.setCancelled(true);
            }
        }

        if (plugin.getConfig().getBoolean("explosive-disabler.minecarts")) {
            if (event.getDamager().getType() == EntityType.TNT_MINECART) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityDamageByBlockEvent event) {
        if (plugin.getConfig().getBoolean("explosive-disabler.crystals")) {
            if (event.getDamagerBlockState() == null)
                return;
            if (event.getDamagerBlockState().getType() == Material.RESPAWN_ANCHOR) {
                event.setCancelled(true);
            }
        }
    }
}
