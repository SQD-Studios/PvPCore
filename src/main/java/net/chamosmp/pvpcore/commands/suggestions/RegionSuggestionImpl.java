package net.chamosmp.pvpcore.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.chamosmp.pvpcore.PvpcorePlugin;
import org.bukkit.configuration.ConfigurationSection;

import java.util.concurrent.CompletableFuture;

public class RegionSuggestionImpl {
    private static PvpcorePlugin plugin;

    public RegionSuggestionImpl(PvpcorePlugin plugin) {
        RegionSuggestionImpl.plugin = plugin;
    }

    @SuppressWarnings("unused")
    @RegionSuggestion
    public static CompletableFuture<Suggestions> methodImplementation(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("safe-zones.regions");
        if (section != null) {
            section.getKeys(false).forEach(builder::suggest);
        }

        return builder.buildFuture();
    }
}