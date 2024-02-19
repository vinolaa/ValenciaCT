package com.vinola.valenciact.listeners;

import com.vinola.valenciact.customitems.Waystone;
import com.vinola.valenciact.storage.PlayerWaystoneDataHandler;
import com.vinola.valenciact.structures.WaystoneStructure;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

import static java.util.UUID.randomUUID;

public class WaystonePlaceListener implements Listener {

    PlayerWaystoneDataHandler playerWaystoneDataHandler = PlayerWaystoneDataHandler.getInstance();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Waystone waystone;
        final WaystoneStructure waystoneStructure;
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        waystone = new Waystone();
        waystoneStructure = new WaystoneStructure();

        if (itemInHand.isSimilar(waystone.getWaystone()) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            event.setCancelled(true);

            List<WaystoneStructure> playerWaystones = playerWaystoneDataHandler.getPlayerWaystones(player.getUniqueId());
            if (playerWaystones != null && playerWaystones.size() >= 6) {
                player.sendMessage("Você já atingiu o limite de waystones.");
                return;
            }

            if (clickedBlock != null) {
                if (waystoneStructure.buildWaystone(clickedBlock, player) == 0) {
                    player.sendMessage("Não é possível construir a estrutura aqui.");
                    return;
                }

                itemInHand.setAmount(itemInHand.getAmount() - 1);
                waystoneStructure.setOwner(player.getUniqueId());
                waystoneStructure.setActivated(false);
                waystoneStructure.setLocation(clickedBlock.getLocation());
                Player dono = Bukkit.getPlayer(waystoneStructure.getOwner());
                assert dono != null;
                List<String> lines = Arrays.asList("§e§lTELEPORTE DE " + dono.getName().toUpperCase() , "§7Teleporte §c§lDESATIVADO§7. Clique para ativar.");

                Location goldLocation = new Location(clickedBlock.getWorld(), clickedBlock.getX(), clickedBlock.getY() + 2, clickedBlock.getZ());

                Location hologramLocation = new Location(goldLocation.getWorld(), goldLocation.getX() + 0.5, goldLocation.getY() + 2.5, goldLocation.getZ() + 0.5);
                Hologram h = DHAPI.createHologram(randomUUID().toString(), hologramLocation, true, lines);
                waystoneStructure.setHologram(h.getName());
                waystoneStructure.setGoldBlock(goldLocation);

                waystoneStructure.setId(randomUUID().toString());

                playerWaystoneDataHandler.addWaystoneToPlayer(player.getUniqueId(), waystoneStructure);
                player.sendMessage("Waystone adicionada.");
            }
        }
    }
}