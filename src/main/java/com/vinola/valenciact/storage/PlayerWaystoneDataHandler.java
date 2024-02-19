package com.vinola.valenciact.storage;

import com.vinola.valenciact.structures.WaystoneStructure;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.*;
import java.util.*;

public class PlayerWaystoneDataHandler {
    private static PlayerWaystoneDataHandler instance = null;
    private final HashMap<UUID, List<WaystoneStructure>> playerWaystonesMap;
    private final List<WaystoneStructure> structureTrash;
    private final Database database = Database.getInstance();

    private PlayerWaystoneDataHandler() {
        playerWaystonesMap = new HashMap<>();
        structureTrash = new ArrayList<>();
    }

    public static PlayerWaystoneDataHandler getInstance() {
        if (instance == null) {
            instance = new PlayerWaystoneDataHandler();
        }
        return instance;
    }

    public List<WaystoneStructure> getPlayerWaystones(UUID playerUUID) {
        return playerWaystonesMap.get(playerUUID);
    }

    public void addWaystoneToPlayer(UUID playerUUID, WaystoneStructure waystone) {
        List<WaystoneStructure> playerWaystones = playerWaystonesMap.computeIfAbsent(playerUUID, k -> new ArrayList<>());
        playerWaystones.add(waystone);
    }

    public List<WaystoneStructure> getAllWaystones() {
        List<WaystoneStructure> allWaystones = new ArrayList<>();
        for (List<WaystoneStructure> playerWaystones : playerWaystonesMap.values()) {
            allWaystones.addAll(playerWaystones);
        }
        return allWaystones;
    }

    public void removeWaystoneFromPlayer(UUID playerUUID, WaystoneStructure waystone) {
        List<WaystoneStructure> playerWaystones = playerWaystonesMap.get(playerUUID);
        if (playerWaystones != null) {
            playerWaystones.remove(waystone);
            structureTrash.add(waystone);
        }
    }

    public void updateWaystone(WaystoneStructure updatedWaystone) {
        UUID owner = updatedWaystone.getOwner();
        List<WaystoneStructure> playerWaystones = playerWaystonesMap.get(owner);
        if (playerWaystones != null) {
            for (int i = 0; i < playerWaystones.size(); i++) {
                WaystoneStructure waystone = playerWaystones.get(i);
                if (waystone.equals(updatedWaystone)) {
                    playerWaystones.set(i, updatedWaystone);
                    break;
                }
            }
        }
    }

    public World getWorldByUUID(UUID uuid) {
        for (World world : Bukkit.getWorlds()) {
            if (world.getUID().equals(uuid)) {
                return world;
            }
        }
        return null;
    }

    public void loadDataFromSQL() {
        try (Connection connection = database.connect()) {
            String selectSql = "SELECT * FROM Players, Structures, Blocks WHERE Players.uuid = Structures.owner AND Structures.id = Blocks.structure_id ORDER BY Structures.id";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                ResultSet resultSet = selectStatement.executeQuery();
                while (resultSet.next()) {
                    UUID playerUUID = UUID.fromString(resultSet.getString("uuid"));
                    WaystoneStructure waystone = new WaystoneStructure();
                    waystone.setId(resultSet.getString("id"));
                    waystone.setOwner(playerUUID);
                    waystone.setActivated(resultSet.getBoolean("status"));
                    waystone.setHologram(resultSet.getString("hologram_id"));
                    waystone.setYaw(resultSet.getInt("yaw"));
                    String worldUUIDString = resultSet.getString("worldName");
                    String currentStructureId = waystone.getId();
                    if (worldUUIDString != null) {
                        UUID worldUUID = UUID.fromString(worldUUIDString);
                        World world = getWorldByUUID(worldUUID);
                        if (world != null) {
                            Location l = new Location(world, resultSet.getInt("xi"), resultSet.getInt("yi"), resultSet.getInt("zi"));
                            waystone.setLocation(l);
                            Location gL = new Location(world, resultSet.getInt("goldBlockX"), resultSet.getInt("goldBlockY"), resultSet.getInt("goldBlockZ"));
                            waystone.setGoldBlock(gL);

                            do {
                                Location blockLocation = new Location(world, resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"));
                                waystone.addBlockLocation(blockLocation);
                            } while (resultSet.next() && resultSet.getString("id").equals(currentStructureId));

                            addWaystoneToPlayer(playerUUID, waystone);
                        } else {
                            System.out.println("World with UUID " + worldUUIDString + " not found");
                        }
                    } else {
                        System.out.println("worldUUID is null");
                    }

                    if (!resultSet.isAfterLast() && !resultSet.getString("id").equals(currentStructureId)) {
                        resultSet.previous();
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void saveDataToSQL() {
        try (Connection connection = database.connect()) {
            for (UUID playerUUID : playerWaystonesMap.keySet()) {
                String insertPlayerSql = "INSERT INTO Players (uuid) VALUES (?) ON DUPLICATE KEY UPDATE uuid=uuid";
                try (PreparedStatement insertPlayerStatement = connection.prepareStatement(insertPlayerSql)) {
                    insertPlayerStatement.setString(1, playerUUID.toString());
                    insertPlayerStatement.executeUpdate();
                }

                List<WaystoneStructure> waystones = playerWaystonesMap.get(playerUUID);
                for (WaystoneStructure waystone : waystones) {
                    String structureId = waystone.getId();
                    String checkSql = "SELECT * FROM Structures WHERE id = ?";
                    try (PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
                        checkStatement.setString(1, structureId);
                        ResultSet resultSet = checkStatement.executeQuery();
                        if (resultSet.next()) {
                            // The structure already exists, update it
                            String updateSql = "UPDATE Structures SET owner = ?, status = ?, hologram_id = ?, yaw = ?, worldName = ?, xi = ?, yi = ?, zi = ?, goldBlockX = ?, goldBlockY = ?, goldBlockZ = ? WHERE id = ?";
                            try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                                updateStatement.setString(1, playerUUID.toString());
                                updateStatement.setBoolean(2, waystone.isActivated());
                                updateStatement.setString(3, waystone.getHologram());
                                Location location = waystone.getLocation();
                                updateStatement.setFloat(4, waystone.getYaw());
                                updateStatement.setString(5, Objects.requireNonNull(location.getWorld()).getUID().toString());
                                updateStatement.setInt(6, (int) location.getX());
                                updateStatement.setInt(7, (int) location.getY());
                                updateStatement.setInt(8, (int) location.getZ());
                                Location goldBlockLocation = waystone.getGoldBlock();
                                updateStatement.setInt(9, (int) goldBlockLocation.getX());
                                updateStatement.setInt(10, (int) goldBlockLocation.getY());
                                updateStatement.setInt(11, (int) goldBlockLocation.getZ());
                                updateStatement.setString(12, structureId);
                                updateStatement.executeUpdate();
                            }
                        } else {
                            // The structure does not exist, insert it
                            String insertSql = "INSERT INTO Structures (id, owner, status, hologram_id, yaw, worldName, xi, yi, zi, goldBlockX, goldBlockY, goldBlockZ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                            try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                                insertStatement.setString(1, structureId);
                                insertStatement.setString(2, playerUUID.toString());
                                insertStatement.setBoolean(3, waystone.isActivated());
                                insertStatement.setString(4, waystone.getHologram());
                                Location location = waystone.getLocation();
                                insertStatement.setFloat(5, waystone.getYaw());
                                insertStatement.setString(6, Objects.requireNonNull(location.getWorld()).getUID().toString());
                                insertStatement.setInt(7, (int) location.getX());
                                insertStatement.setInt(8, (int) location.getY());
                                insertStatement.setInt(9, (int) location.getZ());
                                Location goldBlockLocation = waystone.getGoldBlock();
                                insertStatement.setInt(10, (int) goldBlockLocation.getX());
                                insertStatement.setInt(11, (int) goldBlockLocation.getY());
                                insertStatement.setInt(12, (int) goldBlockLocation.getZ());
                                insertStatement.executeUpdate();
                            }
                        }
                    }

                    for (Location blockLocation : waystone.getBlocksLocations()) {
                        String insertBlocksSql = "INSERT INTO Blocks (structure_id, x, y, z) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement insertBlocksStatement = connection.prepareStatement(insertBlocksSql)) {
                            insertBlocksStatement.setString(1, structureId);
                            insertBlocksStatement.setInt(2, (int) blockLocation.getX());
                            insertBlocksStatement.setInt(3, (int) blockLocation.getY());
                            insertBlocksStatement.setInt(4, (int) blockLocation.getZ());
                            insertBlocksStatement.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void deleteTrashFromSQL() {
        try (Connection connection = database.connect()) {
            for (WaystoneStructure waystone : structureTrash) {
                String structureId = waystone.getId();

                // Delete blocks from Blocks table
                String deleteBlocksSql = "DELETE FROM Blocks WHERE structure_id = ?";
                try (PreparedStatement deleteBlocksStatement = connection.prepareStatement(deleteBlocksSql)) {
                    deleteBlocksStatement.setString(1, structureId);
                    deleteBlocksStatement.executeUpdate();
                }

                // Delete structure from Structures table
                String deleteSql = "DELETE FROM Structures WHERE id = ?";
                try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
                    deleteStatement.setString(1, structureId);
                    deleteStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public List<WaystoneStructure> getStructureTrash() {
        return structureTrash;
    }
}