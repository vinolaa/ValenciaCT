package com.vinola.valenciact.customitems;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PickBreak {
    private final ItemStack pickBreak;

    public PickBreak(){
        pickBreak = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta pickBreakMeta = pickBreak.getItemMeta();
        List<String> pickBreakLore = new ArrayList<>();

        assert pickBreakMeta != null;
        pickBreakMeta.setDisplayName("§eRemovedora de Waystone");
        pickBreakLore.add("§7Quebre qualquer bloco de sua Waystone para removê-la");
        pickBreakMeta.setLore(pickBreakLore);
        pickBreakMeta.setCustomModelData(10004);
        pickBreak.setItemMeta(pickBreakMeta);
    }

    public ItemStack getPickBreak() {
        return pickBreak;
    }
}
