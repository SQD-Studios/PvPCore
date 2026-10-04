package net.chamosmp.pvpcore;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.chamosmp.pvpcore.commands.BaseCommandBrigadier;
import net.chamosmp.pvpcore.commands.suggestions.RegionSuggestionImpl;
import net.chamosmp.pvpcore.listener.CombatListener;
import net.chamosmp.pvpcore.listener.ExplodeListener;
import net.chamosmp.pvpcore.listener.LeaveJoinListener;
import net.chamosmp.pvpcore.listener.RegionEnterListener;
import net.chamosmp.pvpcore.manager.CombatTagManager;
import net.chamosmp.pvpcore.manager.RegionBlockManager;
import net.chamosmp.pvpcore.model.PvpRegion;
import net.chamosmp.pvpcore.papi.PvpcoreExtension;
import net.chamosmp.sqdlib.exceptions.command.CommandRegisterException;
import net.chamosmp.sqdlib.paper.util.ConfigUtil;
import net.chamosmp.sqdlib.paper.util.DebugLogger;
import net.chamosmp.sqdlib.paper.util.LoggerUtil;
import net.chamosmp.sqdlib.util.log.LogType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class PvpcorePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        new LoggerUtil("<dark_purple>PvPCore<white>| ");
        // noinspection all
        new DebugLogger(this);

        // noinspection all
        getDataFolder().mkdirs();
        ConfigurationSerialization.registerClass(PvpRegion.class);
        ConfigUtil.loadOrAdapt(this, "config.yml", List.of("stat-changer.items."));
        LoggerUtil.log(LogType.INFO, "Loaded the config from the disk");

        CombatTagManager tagManager = new CombatTagManager(this);
        RegionBlockManager regionBlockManager = new RegionBlockManager(tagManager);
        RegionBlockManager.reloadRegionsFromConfig(this);

        Bukkit.getPluginManager().registerEvents(new CombatListener(tagManager, this, regionBlockManager), this);
        Bukkit.getPluginManager().registerEvents(new LeaveJoinListener(tagManager, this), this);
        Bukkit.getPluginManager().registerEvents(new ExplodeListener(this), this);
        Bukkit.getPluginManager().registerEvents(new RegionEnterListener(regionBlockManager, this), this);
        LoggerUtil.log(LogType.INFO, "Registered listeners");

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) { //
            new PvpcoreExtension(this, tagManager).register();
            LoggerUtil.log(LogType.INFO, "Registered Placeholders");
        }

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            try {
                // noinspection all
                new RegionSuggestionImpl(this);
                BaseCommandBrigadier.register(event.registrar(), this, regionBlockManager);
                LoggerUtil.log(LogType.INFO, "Successfully registered commands");
            } catch (Exception e) {
                throw new CommandRegisterException("Failed to register commands!", e);
            }
        });
    }
}