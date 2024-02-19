package com.vinola.valenciact.customitems;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Waystone {
    private final ItemStack waystone;

    public Waystone() {
        waystone = new ItemStack(Material.BREWING_STAND);
        ItemMeta waystoneMeta = waystone.getItemMeta();
        assert waystoneMeta != null;
        waystoneMeta.setDisplayName("§6Waystone");
        waystone.setItemMeta(waystoneMeta);
    }

    public ItemStack getWaystone() {
        return waystone;
    }
}
