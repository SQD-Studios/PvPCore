package net.chamosmp.pvpcore.listener;

import net.chamosmp.pvpcore.PvpcorePlugin;
import net.chamosmp.pvpcore.api.event.combat.PlayerWentIntoCombatEvent;
import net.chamosmp.pvpcore.manager.CombatTagManager;
import net.chamosmp.pvpcore.manager.RegionBlockManager;
import net.chamosmp.sqdlib.paper.util.ColorUtil;
import net.chamosmp.sqdlib.paper.util.DebugLogger;
import net.chamosmp.sqdlib.util.log.LogType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicReference;

public class CombatListener implements Listener {

    private final CombatTagManager tagManager;
    private final PvpcorePlugin plugin;
    private final RegionBlockManager regionBlockManager;

    public CombatListener(CombatTagManager tagManager, PvpcorePlugin plugin, RegionBlockManager regionBlockManager) {
        this.tagManager = tagManager;
        this.plugin = plugin;
        this.regionBlockManager = regionBlockManager;
    }

    @EventHandler
    public void onPlayerLeave(PlayerDeathEvent event) {
        tagManager.onPlayerQuit(event);
    }

    @EventHandler
    public void onPlayerWentIntoCombat(EntityDamageByEntityEvent event) {
        // Block players from getting into combat from players into the region, and players inside to go in combat with someone outside
        if (event.getDamager() instanceof Player damager && regionBlockManager.isPlayerInRegion(damager.getLocation()).getFirst()) {
            damager.sendMessage(ColorUtil.parse(
                    plugin.getConfig().getString("safe-zones.in-safe-zone", "")
            ));
            event.setCancelled(true);
            return;
        } else if (event.getEntity() instanceof Player defender && event.getDamager() instanceof Player damager && regionBlockManager.isPlayerInRegion(defender.getLocation()).getFirst()) {
            damager.sendMessage(ColorUtil.parse(
                    plugin.getConfig().getString("safe-zones.in-safe-zone", "")
            ));
            event.setCancelled(true);
            return;
        }

        // Combat Tag
        if (event.getDamager() instanceof Player damager && event.getEntity() instanceof Player defender && !event.isCancelled()) {
            PlayerWentIntoCombatEvent e = new PlayerWentIntoCombatEvent(damager, defender);
            Bukkit.getPluginManager().callEvent(e);

            if (!e.isCancelled()) {
                tagManager.handleCombat(damager, defender);
                tagManager.handleCombat(defender, damager);
            }
        }

        if (plugin.getConfig().getBoolean("stat-changer.enabled")) {
            ItemStack item = event.getDamager() instanceof LivingEntity livingEntity
                    ? livingEntity.getEquipment() != null ? livingEntity.getEquipment().getItemInMainHand() : null
                    : null;
            if (item == null) return;

            double originalDamage = event.getDamage();
            AtomicReference<Double> damage = new AtomicReference<>(event.getDamage());
            ConfigurationSection itemHeldByEntityInConfig = plugin.getConfig().getConfigurationSection("stat-changer.items." + item.getType().toString().toLowerCase());
            if (itemHeldByEntityInConfig != null) {
                if (itemHeldByEntityInConfig.getString("type", "").equalsIgnoreCase("buff")) {
                    damage.updateAndGet(v -> v + itemHeldByEntityInConfig.getDouble("value"));
                } else {
                    damage.updateAndGet(v -> v - itemHeldByEntityInConfig.getDouble("value"));
                }
            }

            item.getEnchantments().keySet().forEach(enchantment -> {
                ConfigurationSection enchant = plugin.getConfig().getConfigurationSection("stat-changer.items." + enchantment.getKey());
                if (enchant == null) {
                    enchant = plugin.getConfig().getConfigurationSection("stat-changer.items." + enchantment.getKey().getKey());
                    if (enchant == null) return;
                }
                final ConfigurationSection finalEnchant = enchant;

                if (enchant.getString("type", "").equalsIgnoreCase("buff")) {
                    damage.updateAndGet(v -> v + finalEnchant.getDouble("value"));
                } else {
                    damage.updateAndGet(v -> v - finalEnchant.getDouble("value"));
                }
            });
            event.setDamage(damage.get());

            DebugLogger.log(LogType.INFO, "Final Damage: " + event.getDamage());
            DebugLogger.log(LogType.INFO, "Original Damage: " + originalDamage);
        }
    }
}