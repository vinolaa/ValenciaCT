package com.vinola.valenciact.listeners;

import com.vinola.valenciact.customitems.Cristal;
import com.vinola.valenciact.customitems.CristalBase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class CraftListener implements Listener {
    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        ItemStack item = event.getRecipe().getResult();
        if (item.isSimilar(new CristalBase().getCristalBase())) {
            event.setCurrentItem(new Cristal().getCristal());
        }
    }
}