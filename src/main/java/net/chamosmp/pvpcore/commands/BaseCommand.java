package net.chamosmp.pvpcore.commands;

import net.chamosmp.pvpcore.PvpcorePlugin;
import net.chamosmp.sqdlib.paper.util.ColorUtil;
import net.strokkur.commands.Command;
import net.strokkur.commands.Executes;
import org.bukkit.command.CommandSender;

@Command("pvpcore")
public class BaseCommand {

    private final PvpcorePlugin plugin;

    public BaseCommand(PvpcorePlugin plugin) {
        this.plugin = plugin;
    }

    @Executes("reload")
    public void reload(CommandSender sender) {
        plugin.reloadConfig();
        sender.sendMessage(ColorUtil.parse("<green>Successfully reloaded!"));
    }
}