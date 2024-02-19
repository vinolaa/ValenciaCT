package com.vinola.valenciact.listeners;

import com.vinola.valenciact.customitems.PickBreak;
import com.vinola.valenciact.customitems.Waystone;
import com.vinola.valenciact.storage.PlayerWaystoneDataHandler;
import com.vinola.valenciact.structures.WaystoneStructure;
import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class StructureBreak implements Listener {

    PlayerWaystoneDataHandler playerWaystoneDataHandler = PlayerWaystoneDataHandler.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        List<WaystoneStructure> allWaystones = playerWaystoneDataHandler.getAllWaystones();
        if (allWaystones != null) {
            for (WaystoneStructure waystone : allWaystones) {
                if (waystone.getBlocksLocations().contains(block.getLocation())) {
                    if (waystone.getOwner().equals(player.getUniqueId())) {
                        if (itemInHand.isSimilar(new PickBreak().getPickBreak())) {
                            waystone.getBlocksLocations().forEach(location -> location.getBlock().setType(Material.AIR));
                            Objects.requireNonNull(DHAPI.getHologram(waystone.getHologram())).delete();
                            playerWaystoneDataHandler.removeWaystoneFromPlayer(player.getUniqueId(), waystone);
                            event.setCancelled(true);
                            ItemStack nw = new Waystone().getWaystone();
                            player.getInventory().addItem(nw);
                            player.sendMessage("§cTotem de teleporte removido com sucesso!");
                        } else {
                            player.sendMessage("§cUse o item correto (Removedora de totens) para quebrar seu totem!");
                            event.setCancelled(true);
                            return;
                        }
                    } else {
                        player.sendMessage("§cApenas o proprietário pode remover o totem!");
                        event.setCancelled(true);
                    }
                    return;
                }
            }
        }
    }
}