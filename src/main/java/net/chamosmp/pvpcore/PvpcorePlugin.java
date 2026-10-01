package net.chamosmp.pvpcore;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.chamosmp.pvpcore.commands.BaseCommandBrigadier;
import net.chamosmp.pvpcore.listener.CombatListener;
import net.chamosmp.pvpcore.listener.ExplodeListener;
import net.chamosmp.pvpcore.listener.LeaveJoinListener;
import net.chamosmp.pvpcore.manager.CombatTagManager;
import net.chamosmp.sqdlib.exceptions.command.CommandRegisterException;
import net.chamosmp.sqdlib.paper.util.ConfigUtil;
import net.chamosmp.sqdlib.paper.util.DebugLogger;
import net.chamosmp.sqdlib.paper.util.LoggerUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class PvpcorePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        new LoggerUtil("<dark_purple>PvPCore<white>| ");
        new DebugLogger(this);

        getDataFolder().mkdirs();
        ConfigUtil.loadOrAdapt(this, "config.yml", List.of("stat-changer.items."));

        CombatTagManager tagManager = new CombatTagManager(this);
        Bukkit.getPluginManager().registerEvents(new CombatListener(tagManager, this), this);
        Bukkit.getPluginManager().registerEvents(new LeaveJoinListener(tagManager, this), this);
        Bukkit.getPluginManager().registerEvents(new ExplodeListener(this), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            try {
                BaseCommandBrigadier.register(event.registrar(), this);
            } catch (Exception e) {
                throw new CommandRegisterException("Failed to register commands!", e);
            }
        });
    }
}