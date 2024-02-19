package com.vinola.valenciact.guis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class NoWaystoneGUI {

    public Inventory createGUI() {
        Inventory gui = Bukkit.createInventory(null, 27, "Sem waystones");

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta barrierMeta = barrier.getItemMeta();

        assert barrierMeta != null;
        barrierMeta.setDisplayName("§cNenhum local marcado");
        barrier.setItemMeta(barrierMeta);

        gui.setItem(13, barrier);

        return gui;
    }
}