package net.chamosmp.pvpcore;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.chamosmp.pvpcore.commands.BaseCommandBrigadier;
import net.chamosmp.pvpcore.listener.CombatListener;
import net.chamosmp.pvpcore.listener.LeaveJoinListener;
import net.chamosmp.pvpcore.manager.CombatTagManager;
import net.chamosmp.sqdlib.exceptions.CommandRegisterException;
import net.chamosmp.sqdlib.paper.util.ConfigUtil;
import net.chamosmp.sqdlib.paper.util.LoggerUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PvpcorePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        new LoggerUtil("<dark_purple>PvPCore<white>| ");

        getDataFolder().mkdirs();
        ConfigUtil.loadOrAdapt(this, "config.yml");

        CombatTagManager tagManager = new CombatTagManager(this);
        Bukkit.getPluginManager().registerEvents(new CombatListener(tagManager), this);
        Bukkit.getPluginManager().registerEvents(new LeaveJoinListener(tagManager, this), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            try {
                BaseCommandBrigadier.register(event.registrar(), this);
            } catch (Exception e) {
                throw new CommandRegisterException("Failed to register commands!", e);
            }
        });
    }
}