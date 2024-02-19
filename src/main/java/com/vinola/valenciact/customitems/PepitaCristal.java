package com.vinola.valenciact.customitems;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PepitaCristal {
    private final ItemStack pepCristal;

    public PepitaCristal() {
        pepCristal = new ItemStack(Material.PAPER);
        ItemMeta pepCristalMeta = pepCristal.getItemMeta();
        assert pepCristalMeta != null;
        pepCristalMeta.setDisplayName("§dPepita de Cristal");
        pepCristalMeta.setCustomModelData(10069);
        pepCristal.setItemMeta(pepCristalMeta);
    }

    public ItemStack getPepCristal() {
        return pepCristal;
    }

}
