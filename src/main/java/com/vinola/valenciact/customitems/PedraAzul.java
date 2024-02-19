package com.vinola.valenciact.customitems;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PedraAzul {
    private final ItemStack pedraAzul;

    public PedraAzul(){
        pedraAzul = new ItemStack(Material.PAPER);
        ItemMeta pedraAzulMeta = pedraAzul.getItemMeta();

        assert pedraAzulMeta != null;
        pedraAzulMeta.setDisplayName("§bPedra Mágica");
        pedraAzulMeta.setCustomModelData(10071);
        pedraAzul.setItemMeta(pedraAzulMeta);
    }

    public ItemStack getPedraAzul() {
        return pedraAzul;
    }
}
