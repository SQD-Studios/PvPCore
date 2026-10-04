package net.chamosmp.pvpcore;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.World;
import net.chamosmp.pvpcore.manager.RegionBlockManager;
import net.chamosmp.pvpcore.model.PvpRegion;
import net.chamosmp.sqdlib.paper.util.ColorUtil;
import net.chamosmp.sqdlib.paper.util.LoggerUtil;
import net.chamosmp.sqdlib.util.log.LogType;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public class WorldEditIntegration {

    public WorldEditIntegration() {
        LoggerUtil.log(LogType.INFO, "Successfully loaded the WorldEdit integration");
    }

    @Nullable
    public Region getRegion(Player player) {
        com.sk89q.worldedit.entity.Player actor = BukkitAdapter.adapt(player);
        LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(actor);

        World selectionWorld = localSession.getSelectionWorld();
        try {
            if (selectionWorld == null) throw new IncompleteRegionException();
            return localSession.getSelection(selectionWorld);
        } catch (IncompleteRegionException ex) {
            actor.printError(TextComponent.of("Please make a region selection first."));
        }
        return null;
    }

    public @Nullable PvpRegion getPvpRegion(Player player) {
        if (!RegionBlockManager.canAccessWe()) {
            player.sendMessage(ColorUtil.parse("<red>You need to have WorldEdit to use the region blocking features"));
        }
        Region region = getRegion(player);
        if (region == null) return null;
        return PvpRegion.getFromRegion(region);
    }
}