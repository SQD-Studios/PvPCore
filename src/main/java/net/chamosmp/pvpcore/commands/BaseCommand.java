package net.chamosmp.pvpcore.commands;

import net.chamosmp.pvpcore.PvpcorePlugin;
import net.chamosmp.pvpcore.commands.suggestions.RegionSuggestion;
import net.chamosmp.pvpcore.manager.RegionBlockManager;
import net.chamosmp.pvpcore.model.PvpRegion;
import net.chamosmp.sqdlib.paper.util.ColorUtil;
import net.chamosmp.sqdlib.paper.util.LoggerUtil;
import net.chamosmp.sqdlib.util.log.LogType;
import net.strokkur.commands.Command;
import net.strokkur.commands.Executes;
import net.strokkur.commands.Subcommand;
import net.strokkur.commands.paper.Executor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

@Command("pvpcore")
public class BaseCommand {

    private final PvpcorePlugin plugin;
    private final RegionBlockManager regionBlockManager;

    public BaseCommand(PvpcorePlugin plugin, RegionBlockManager regionBlockManager) {
        this.plugin = plugin;
        this.regionBlockManager = regionBlockManager;
    }

    @Executes("reload")
    public void reload(CommandSender sender) {
        plugin.reloadConfig();
        RegionBlockManager.reloadRegionsFromConfig(plugin);
        sender.sendMessage(ColorUtil.parse("<green>Successfully reloaded!"));
    }

    @Subcommand("region")
    public class RegionCommands {
        @Executes("add")
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public void addRegion(@Executor Player sender, Optional<String> regionName) {
            if (regionBlockManager.getWorldEditIntegration() == null) return;
            PvpRegion region = regionBlockManager.getWorldEditIntegration().getPvpRegion(sender);
            if (region == null) return;
            plugin.getConfig().set("safe-zones.regions." + regionName.orElse(UUID.randomUUID().toString()), region);
            saveAndReload(sender);
            sender.sendMessage(ColorUtil.parse("<green>Successfully added region!"));
        }

        @Executes("remove")
        public void removeRegion(@Executor Player sender, @RegionSuggestion String region) {
            plugin.getConfig().set("safe-zones.regions." + region, null);
            saveAndReload(sender);
            sender.sendMessage(ColorUtil.parse("<green>Successfully removed region!"));
        }

        @Executes("replace")
        public void replaceRegion(@Executor Player sender, @RegionSuggestion String region) {
            if (regionBlockManager.getWorldEditIntegration() == null) return;
            PvpRegion worldEditRegion = regionBlockManager.getWorldEditIntegration().getPvpRegion(sender);
            if (worldEditRegion == null) return;
            plugin.getConfig().set("safe-zones.regions." + region, worldEditRegion);
            saveAndReload(sender);
            sender.sendMessage(ColorUtil.parse("<green>Successfully replaced region!"));
        }

        private void saveAndReload(CommandSender sender) {
            try {
                plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
                plugin.reloadConfig();
                RegionBlockManager.reloadRegionsFromConfig(plugin);
            } catch (Exception e) {
                LoggerUtil.log(LogType.SEVERE, e.toString());
                sender.sendMessage(ColorUtil.parse("<red>Failed to save and reload config!"));
                sender.sendMessage(e.getMessage());
            }
        }
    }
}