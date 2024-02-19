package com.vinola.valenciact.storage;

import com.vinola.valenciact.ValenciaCT;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CristalDataHandler {

    private static CristalDataHandler instance = null;
    private final Database database = Database.getInstance();
    private final HashMap<UUID, Integer> cristalUses;

    public CristalDataHandler() {
        cristalUses = new HashMap<>();
    }

    public static CristalDataHandler getInstance() {
        if (instance == null) {
            instance = new CristalDataHandler();
        }
        return instance;
    }

    public int getUses(UUID cristalUUID) {
        return cristalUses.get(cristalUUID);
    }

    public HashMap<UUID, Integer> getCristalUses() {
        return cristalUses;
    }

    public void setUses(UUID cristalUUID, int uses) {
        cristalUses.put(cristalUUID, uses);
    }

    public void decrementUses(UUID cristalUUID, Player player) {
        if (cristalUses.containsKey(cristalUUID)) {
            int uses = cristalUses.get(cristalUUID);
            if (uses > 1) {
                cristalUses.put(cristalUUID, uses - 1);
                updateCristalName(cristalUUID, player);
            } else {
                cristalUses.remove(cristalUUID);
                removeCristalFromPlayer(cristalUUID, player);
            }
        }
    }

    private void updateCristalName(UUID cristalUUID, Player player) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = itemInHand.getItemMeta();
        if (itemInHand.getType() == Material.DIAMOND && itemMeta != null && itemMeta.getDisplayName().contains("Cristal de teleporte")) {
            NamespacedKey key = new NamespacedKey(ValenciaCT.getInstance(), "cristalUUID");
            String cristalUUIDString = itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            if (cristalUUIDString != null && cristalUUIDString.equals(cristalUUID.toString())) {
                itemMeta.setDisplayName("§bCristal de teleporte §7(Usos restantes: §b" + cristalUses.get(cristalUUID) + "§7)");
                itemInHand.setItemMeta(itemMeta);
            }
        }
    }

    private void removeCristalFromPlayer(UUID cristalUUID, Player player) {
        ItemStack[] inventoryContents = player.getInventory().getContents();
        for (int i = 0; i < inventoryContents.length; i++) {
            ItemStack item = inventoryContents[i];
            if (item != null && item.getType() == Material.DIAMOND) {
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta != null && itemMeta.getDisplayName().contains("Cristal de teleporte")) {
                    NamespacedKey key = new NamespacedKey(ValenciaCT.getInstance(), "cristalUUID");
                    String cristalUUIDString = itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                    if (cristalUUIDString != null && cristalUUIDString.equals(cristalUUID.toString())) {
                        player.getInventory().setItem(i, null);
                        player.sendMessage("§cSeu cristal de teleporte foi consumido.");
                        break;
                    }
                }
            }
        }
    }

    public void loadDataFromSQL() {
        try {
            Connection connection = database.connect();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM CristalUses");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                UUID cristalUUID = UUID.fromString(resultSet.getString("uuid"));
                int uses = resultSet.getInt("uses");
                cristalUses.put(cristalUUID, uses);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void saveDataToSQL() {
        try {
            Connection connection = database.connect();
            PreparedStatement statement = connection.prepareStatement("REPLACE INTO CristalUses (uuid, uses) VALUES (?, ?)");

            for (Map.Entry<UUID, Integer> entry : cristalUses.entrySet()) {
                statement.setString(1, entry.getKey().toString());
                statement.setInt(2, entry.getValue());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}