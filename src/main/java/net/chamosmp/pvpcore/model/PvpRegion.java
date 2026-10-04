package net.chamosmp.pvpcore.model;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PvpRegion implements ConfigurationSerializable {

    private final String world;
    private final double minX;
    private final double minY;
    private final double minZ;
    private final double maxX;
    private final double maxY;
    private final double maxZ;

    public PvpRegion(Location pos1, Location pos2) {
        this.world = pos1.getWorld().getName();

        this.minX = Math.min(pos1.getX(), pos2.getX());
        this.minY = Math.min(pos1.getY(), pos2.getY());
        this.minZ = Math.min(pos1.getZ(), pos2.getZ());
        this.maxX = Math.max(pos1.getX(), pos2.getX());
        this.maxY = Math.max(pos1.getY(), pos2.getY());
        this.maxZ = Math.max(pos1.getZ(), pos2.getZ());
    }

    /**
     * This only exists for the {@link ConfigurationSerializable}
     *
     * @param map the map provided
     */
    @SuppressWarnings("unused")
    public PvpRegion(Map<String, Object> map) {
        this.world = (String) map.get("worldName");

        this.minX = (Double) map.get("minX");
        this.minY = (Double) map.get("minY");
        this.minZ = (Double) map.get("minZ");
        this.maxX = (Double) map.get("maxX");
        this.maxY = (Double) map.get("maxY");
        this.maxZ = (Double) map.get("maxZ");
    }

    @Override
    public @NonNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("worldName", this.world);
        map.put("minX", this.minX);
        map.put("minY", this.minY);
        map.put("minZ", this.minZ);
        map.put("maxX", this.maxX);
        map.put("maxY", this.maxY);
        map.put("maxZ", this.maxZ);

        return map;
    }

    public static PvpRegion getFromRegion(Region region) {
        World world = BukkitAdapter.adapt(Objects.requireNonNull(region.getWorld()));

        BlockVector3 vector1 = region.getBoundingBox().getPos1();
        BlockVector3 vector2 = region.getBoundingBox().getPos2();

        return new PvpRegion(
                new Location(world, vector1.x(), vector1.y(), vector1.z()),
                new Location(world, vector2.x(), vector2.y(), vector2.z())
        );
    }

    public boolean contains(Location location) {
        if (location != null && location.getWorld() != null && location.getWorld().getName().equals(this.world)) {
            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();
            return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY && z >= this.minZ && z <= this.maxZ;
        } else {
            return false;
        }
    }
}
