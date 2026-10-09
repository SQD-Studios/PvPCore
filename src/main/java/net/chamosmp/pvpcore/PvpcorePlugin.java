package net.chamosmp.pvpcore;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.chamosmp.pvpcore.api.PvpCoreApi;
import net.chamosmp.pvpcore.api.model.PvpRegion;
import net.chamosmp.pvpcore.api.services.CombatTagService;
import net.chamosmp.pvpcore.api.services.RegionBlockService;
import net.chamosmp.pvpcore.commands.BaseCommandBrigadier;
import net.chamosmp.pvpcore.commands.suggestions.RegionSuggestionImpl;
import net.chamosmp.pvpcore.listener.CombatListener;
import net.chamosmp.pvpcore.listener.ExplodeListener;
import net.chamosmp.pvpcore.listener.LeaveJoinListener;
import net.chamosmp.pvpcore.listener.RegionEnterListener;
import net.chamosmp.pvpcore.manager.CombatTagManager;
import net.chamosmp.pvpcore.manager.RegionBlockManager;
import net.chamosmp.pvpcore.papi.PvpcoreExtension;
import net.chamosmp.sqdlib.exceptions.command.CommandRegisterException;
import net.chamosmp.sqdlib.paper.util.ConfigUtil;
import net.chamosmp.sqdlib.paper.util.DebugLogger;
import net.chamosmp.sqdlib.paper.util.LoggerUtil;
import net.chamosmp.sqdlib.util.log.LogType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class PvpcorePlugin extends JavaPlugin implements PvpCoreApi {
    private CombatTagManager combatTagManager;
    private RegionBlockManager regionBlockManager;

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

        combatTagManager = new CombatTagManager(this);
        regionBlockManager = new RegionBlockManager(combatTagManager);
        RegionBlockManager.reloadRegionsFromConfig(this);

        Bukkit.getServicesManager().register(PvpCoreApi.class, this, this, ServicePriority.Normal);

        Bukkit.getPluginManager().registerEvents(new CombatListener(combatTagManager, this, regionBlockManager), this);
        Bukkit.getPluginManager().registerEvents(new LeaveJoinListener(combatTagManager, this), this);
        Bukkit.getPluginManager().registerEvents(new ExplodeListener(this), this);
        Bukkit.getPluginManager().registerEvents(new RegionEnterListener(regionBlockManager, this, combatTagManager), this);
        LoggerUtil.log(LogType.INFO, "Registered listeners");

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) { //
            new PvpcoreExtension(this, combatTagManager).register();
            LoggerUtil.log(LogType.INFO, "Registered Placeholders");
        }

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            try {
                // noinspection all
                new RegionSuggestionImpl(this);
                BaseCommandBrigadier.register(event.registrar(), this, regionBlockManager, combatTagManager);
                LoggerUtil.log(LogType.INFO, "Successfully registered commands");
            } catch (Exception e) {
                throw new CommandRegisterException("Failed to register commands!", e);
            }
        });
    }

    @Override
    public @NonNull CombatTagService getCombatTagService() {
        return combatTagManager;
    }

    @Override
    public @NonNull RegionBlockService getRegionBlockService() {
        return regionBlockManager;
    }
}