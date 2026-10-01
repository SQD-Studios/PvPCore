package net.chamosmp.pvpcore.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.chamosmp.pvpcore.PvpcorePlugin;
import net.chamosmp.pvpcore.manager.CombatTagManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PvpcoreExtension extends PlaceholderExpansion {

    private final PvpcorePlugin plugin;
    private final CombatTagManager combatTagManager;

    public PvpcoreExtension(PvpcorePlugin plugin, CombatTagManager combatTagManager) {
        this.plugin = plugin;
        this.combatTagManager = combatTagManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return plugin.getPluginMeta().getName().toLowerCase();
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getPluginMeta().getAuthors().getFirst();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.startsWith("ct_isincombat") && player != null) {
            return String.valueOf(combatTagManager.isInCombat(player));
        }
        if (params.startsWith("ct_isincombat_")) {
            String param = params.substring(14);
            Player target = Bukkit.getPlayerExact(param);
            if (target != null) {
                return String.valueOf(combatTagManager.isInCombat(target));
            }
        }

        if (params.startsWith("ct_timeleft")) {
            if (player != null) {
                return String.valueOf(combatTagManager.getTaggedPlayer(player).inCombatFor());
            }
        }
        if (params.startsWith("ct_timeleft_")) {
            String param = params.substring(12);
            Player target = Bukkit.getPlayerExact(param);
            if (target != null) {
                return String.valueOf(combatTagManager.getTaggedPlayer(target).inCombatFor());
            }
        }

        if (params.startsWith("explosives_")) {
            String param = params.substring(11);
            if (param.equals("crystals")) {
                return plugin.getConfig().getString("explosive-disabler.crystals", "false");
            } else if (param.equals("minecarts")) {
                return plugin.getConfig().getString("explosive-disabler.minecarts", "false");
            }
        }

        return null;
    }
}
