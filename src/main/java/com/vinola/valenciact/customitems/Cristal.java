package com.vinola.valenciact.customitems;

import com.vinola.valenciact.ValenciaCT;
import com.vinola.valenciact.storage.CristalDataHandler;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cristal {
    private final ItemStack cristal;
    private UUID cristalUUID;

    public Cristal(){
        cristal = new ItemStack(Material.PAPER);
        ItemMeta cristalMeta = cristal.getItemMeta();
        List<String> cristalLore = new ArrayList<>();
        assert cristalMeta != null;
        CristalDataHandler cdh = CristalDataHandler.getInstance();
        cristalUUID = UUID.randomUUID();
        cdh.setUses(cristalUUID, 2);
        NamespacedKey key = new NamespacedKey(ValenciaCT.getInstance(), "cristalUUID");
        cristalMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, cristalUUID.toString());
        cristalMeta.setDisplayName("§bCristal de teleporte §7(Usos restantes: §b" + cdh.getUses(cristalUUID) + "§7)");
        cristalLore.add("§7Para se teleportar à algum lugar, clique com o botão direito");
        cristalLore.add("");
        cristalLore.add("§ePara fazer um teleporte para alguma das suas marcações, clique com o botão esquerdo");
        cristalMeta.setLore(cristalLore);
        cristalMeta.setCustomModelData(10070);
        cristal.setItemMeta(cristalMeta);
    }

    public ItemStack getCristal() {
        return cristal;
    }

    public UUID getCristalUUID() {
        return cristalUUID;
    }

    public void setCristalUUID(UUID cristalUUID) {
        this.cristalUUID = cristalUUID;
    }
}