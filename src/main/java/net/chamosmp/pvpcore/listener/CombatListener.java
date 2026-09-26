package net.chamosmp.pvpcore.listener;

import net.chamosmp.pvpcore.manager.CombatTagManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CombatListener implements Listener {

    private final CombatTagManager tagManager;

    public CombatListener(CombatTagManager tagManager) {
        this.tagManager = tagManager;
    }

    @EventHandler
    public void onPlayerLeave(PlayerDeathEvent event) {
        tagManager.onPlayerQuit(event);
    }

    @EventHandler
    public void onPlayerWentIntoCombat(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager && event.getEntity() instanceof Player defender
                && tagManager.isCombatTagEnabled()) {
            tagManager.handleCombat(damager, defender);
            tagManager.handleCombat(defender, damager);
        }
    }
}
