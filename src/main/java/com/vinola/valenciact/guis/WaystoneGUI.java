package com.vinola.valenciact.guis;

import com.vinola.valenciact.customitems.PedraAzul;
import com.vinola.valenciact.storage.PlayerWaystoneDataHandler;
import com.vinola.valenciact.structures.WaystoneStructure;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class WaystoneGUI {

    PlayerWaystoneDataHandler playerWaystoneDataHandler = PlayerWaystoneDataHandler.getInstance();

    public Inventory createGUI(UUID playerUUID) {
        Inventory gui = Bukkit.createInventory(null, 27, "Waystones");

        gui.clear(10);

        List<WaystoneStructure> waystones = playerWaystoneDataHandler.getPlayerWaystones(playerUUID);
        int numWaystones = (waystones != null) ? waystones.size() : 0;
        ItemStack[] items = new ItemStack[6];

        ItemStack pedraAzul = new PedraAzul().getPedraAzul();

        for (int i = 0; i < 6; i++) {
            items[i] = new ItemStack(Material.BARRIER);
        }
        List<String> l = new ArrayList<>();
        String status;
        String aux;

        for (int i = 0; i < numWaystones; i++) {
            if (waystones.get(i).isActivated()) {
                status = "§7Status: §a§lATIVADO";
                l.add(status);
                l.add(" ");
            } else {
                status = "§7Status: §c§lDESATIVADO";
                l.add(status);
            }
            String w = Objects.requireNonNull(waystones.get(i).getLocation().getWorld()).getName();
            int x = (int) waystones.get(i).getLocation().getX();
            int y = (int) waystones.get(i).getLocation().getY();
            int z = (int) waystones.get(i).getLocation().getZ();
            l.add("§7Mundo: §b§l" + w + " §7X: §b§l" + x + " §7Y: §b§l" + y + " §7Z: §b§l" + z);
            l.add(" ");
            aux = "§eClique para teleportar";
            l.add(aux);

            //items[i] = new ItemStack(Material.GOLD_INGOT);
            items[i] = pedraAzul.clone();
            ItemMeta meta = items[i].getItemMeta();
            assert meta != null;
            meta.setDisplayName("§bWaystone " + (i + 1));
            if (waystones.get(i).getYaw() == 0) {
                meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            }else if (waystones.get(i).getYaw() == 90) {
                meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
            }else if (waystones.get(i).getYaw() == -90) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }else {
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            }
            meta.setLore(l);
            items[i].setItemMeta(meta);
            l.clear();
        }

        int lastGoldIngotIndex = -1;
        for (int i = 0; i < 6; i++) {
            if (items[i].getType() == pedraAzul.getType()) {
                gui.setItem(10 + i, items[i]);
                lastGoldIngotIndex = i;
            }
        }
        for (int i = lastGoldIngotIndex + 1; i < 6; i++) {
            if (items[i].getType() == Material.BARRIER) {
                gui.setItem(11 + i, items[i]);
            }
        }

        return gui;
    }
}