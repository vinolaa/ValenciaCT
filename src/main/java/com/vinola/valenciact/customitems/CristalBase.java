package com.vinola.valenciact.customitems;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CristalBase {
    private final ItemStack cristalBase;

    public CristalBase(){
        cristalBase = new ItemStack(Material.PAPER);
        ItemMeta cristalMeta = cristalBase.getItemMeta();
        List<String> cristalLore = new ArrayList<>();
        assert cristalMeta != null;
        cristalMeta.setDisplayName("§bCristal de teleporte §7(Usos restantes: §b" + 2 + "§7)");
        cristalMeta.setCustomModelData(10070);
        cristalLore.add("§7Para se teleportar à algum lugar, clique com o botão direito");
        cristalLore.add("");
        cristalLore.add("§ePara fazer um teleporte para alguma das suas marcações, clique com o botão esquerdo");
        cristalMeta.setLore(cristalLore);
        cristalBase.setItemMeta(cristalMeta);
    }

    public ItemStack getCristalBase() {
        return cristalBase;
    }
}