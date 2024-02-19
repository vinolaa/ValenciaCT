package com.vinola.valenciact.structures;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WaystoneStructure {

    private String id;

    private UUID owner;

    private boolean isActivated;

    private Location location;

    private String hologram;

    private float yaw;

    private Location goldBlock;

    private List<Location> blocksLocations = new ArrayList<>();

    public WaystoneStructure() {
        this.owner = null;
        this.isActivated = false;
        this.location = null;
        this.hologram = null;
        this.goldBlock = null;
        this.yaw = 0;
    }

    public int buildWaystone(Block block, Player player){
        if (!canBuildWaystone(block)) {
            return 0;
        }

        float yaw = player.getLocation().getYaw();
        yaw = yaw < 0 ? yaw + 360 : yaw;

        if (yaw >= 45 && yaw < 135) {
            buildWestFacingStructure(block);
            this.yaw = -90;
            player.sendMessage("Build West");
        } else if (yaw >= 135 && yaw < 225) {
            buildNorthFacingStructure(block);
            this.yaw = 0;
            player.sendMessage("Build North");
        } else if (yaw >= 225 && yaw < 315) {
            buildEastFacingStructure(block);
            this.yaw = 90;
            player.sendMessage("Build East");
        } else {
            buildSouthFacingStructure(block);
            this.yaw = 180;
            player.sendMessage("Build South");
        }

        return 1;
    }

    private boolean canBuildWaystone(Block block) {
        for (int x = -1; x <= 1; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block relativeBlock = block.getRelative(x, y, z);
                    if (relativeBlock.getType() != Material.AIR) return false;
                }
            }
        }
        return true;
    }

    private void buildWestFacingStructure(Block block) {
        buildStructure(block, BlockFace.EAST);
    }

    private void buildNorthFacingStructure(Block block) {
        buildStructure(block, BlockFace.SOUTH);
    }

    private void buildEastFacingStructure(Block block) {
        buildStructure(block, BlockFace.WEST);
    }

    private void buildSouthFacingStructure(Block block) {
        buildStructure(block, BlockFace.NORTH);
    }

    private void buildStructure(Block block, BlockFace direction) {
        switch (direction) {
            case EAST: // P W
                buildStructureEast(block);
                break;
            case SOUTH: // P N
                buildStructureSouth(block);
                break;
            case WEST: // P E
                buildStructureWest(block);
                break;
            default: // P S
                buildStructureNorth(block);
                break;
        }
    }

    private void buildBaseStructure(Block block) {
        block.getRelative(0, 1, 0).setType(Material.STONE_BRICKS);
        addBlockLocation(block.getRelative(0, 1, 0).getLocation());

        block.getRelative(0, 2, 0).setType(Material.GOLD_BLOCK);
        addBlockLocation(block.getRelative(0, 2, 0).getLocation());

        block.getRelative(0, 3, 0).setType(Material.STONE_BRICK_SLAB);
        addBlockLocation(block.getRelative(0, 3, 0).getLocation());
    }

    private void setLadder(Block block, BlockFace direction) {
        block.setType(Material.STONE_BRICK_STAIRS);
        addBlockLocation(block.getLocation());
        Stairs stairs = (Stairs) block.getBlockData();
        stairs.setFacing(direction);
        stairs.setHalf(Stairs.Half.TOP);
        block.setBlockData(stairs);
    }

    private void buildStructureNorth(Block block) {
        buildBaseStructure(block);
        setLadder(block.getRelative(1, 2, 0), BlockFace.WEST);
        setLadder(block.getRelative(-1, 2, 0), BlockFace.EAST);
    }

    private void buildStructureEast(Block block) {
        buildBaseStructure(block);
        setLadder(block.getRelative(0, 2, -1), BlockFace.SOUTH);
        setLadder(block.getRelative(0, 2, 1), BlockFace.NORTH);
    }

    private void buildStructureSouth(Block block) {
        buildBaseStructure(block);
        setLadder(block.getRelative(1, 2, 0), BlockFace.WEST);
        setLadder(block.getRelative(-1, 2, 0), BlockFace.EAST);
    }

    private void buildStructureWest(Block block) {
        buildBaseStructure(block);
        setLadder(block.getRelative(0, 2, 1), BlockFace.NORTH);
        setLadder(block.getRelative(0, 2, -1), BlockFace.SOUTH);
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getHologram() {
        return hologram;
    }

    public void setHologram(String hologram) {
        this.hologram = hologram;
    }

    public Location getGoldBlock() {
        return goldBlock;
    }

    public void setGoldBlock(Location goldBlock) {
        this.goldBlock = goldBlock;
    }

    public List<Location> getBlocksLocations() {
        return blocksLocations;
    }

    public void addBlockLocation(Location location) {
        this.blocksLocations.add(location);
    }

    public void setBlocksLocations(List<Location> blockLocations) {
        this.blocksLocations = blockLocations;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}