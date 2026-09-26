package net.chamosmp.pvpcore.listener;

import net.chamosmp.pvpcore.PvpcorePlugin;
import net.chamosmp.pvpcore.manager.CombatTagManager;
import net.chamosmp.sqdlib.paper.util.ColorUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class PlayerCommandListener implements Listener {

    private final PvpcorePlugin plugin;
    private final CombatTagManager combatTracker;

    public PlayerCommandListener(PvpcorePlugin plugin, CombatTagManager combatTracker) {
        this.plugin = plugin;
        this.combatTracker = combatTracker;
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        if (!plugin.getConfig().getBoolean("combat-tag.command-block.enabled")) return;
        if (!combatTracker.isInCombat(e.getPlayer())) return;

        List<String> blockedCommands = plugin.getConfig().getStringList("combat-tag.command-block.blocked-cmds");

        String cancelledMessage = plugin.getConfig().getString("combat-tag.command-block.blocked-message");

        String[] args = e.getMessage().split(" ");
        args[0] = "/" + args[0].split(":")[1];
        String message = String.join(" ", args);

        if (plugin.getConfig().getBoolean("combat-tag.command-block.match-entire-words")) {
            if (blockedCommands.contains(message.toLowerCase())) {
                e.setCancelled(true);
                if (cancelledMessage != null)
                    e.getPlayer().sendMessage(ColorUtil.parse(cancelledMessage));
            }
        } else {
            blockedCommands.forEach(cmd -> {
                String[] blockedArgs = cmd.split(" ");
                boolean match = true;

                for (int i = 0; i < blockedArgs.length; i++) {
                    if (i >= args.length || !args[i].equalsIgnoreCase(blockedArgs[i])) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    e.setCancelled(true);
                    if (cancelledMessage != null)
                        e.getPlayer().sendMessage(ColorUtil.parse(cancelledMessage));
                }
            });
        }
    }
}
