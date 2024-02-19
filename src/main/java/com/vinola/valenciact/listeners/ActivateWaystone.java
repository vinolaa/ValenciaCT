package com.vinola.valenciact.listeners;

import com.vinola.valenciact.storage.PlayerWaystoneDataHandler;
import com.vinola.valenciact.structures.WaystoneStructure;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ActivateWaystone implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        PlayerWaystoneDataHandler playerWaystoneDataHandler = PlayerWaystoneDataHandler.getInstance();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && Objects.requireNonNull(event.getClickedBlock()).getType() == Material.GOLD_BLOCK && event.getHand() == EquipmentSlot.HAND) {
            UUID playerUUID = event.getPlayer().getUniqueId();
            List<WaystoneStructure> playerWaystones = playerWaystoneDataHandler.getPlayerWaystones(playerUUID);

            if (playerWaystones != null) {
                for (WaystoneStructure waystone : playerWaystones) {
                    if (waystone.getGoldBlock().equals(event.getClickedBlock().getLocation()) && !waystone.isActivated()) {
                        event.getPlayer().sendMessage("Waystone ativado com sucesso!");
                        waystone.setActivated(true);
                        Hologram hologram = DHAPI.getHologram(waystone.getHologram());
                        if (hologram != null) {
                            DHAPI.removeHologramLine(hologram, 1);
                            DHAPI.addHologramLine(hologram, 0, "§7Teleporte §a§lATIVADO§7. Destrua o teleporte para desativar.");
                        }
                        playerWaystoneDataHandler.updateWaystone(waystone);
                        return;
                    }
                }
            }

            List<WaystoneStructure> allWaystones = playerWaystoneDataHandler.getAllWaystones();
            if (allWaystones != null) {
                for (WaystoneStructure waystone : allWaystones) {
                    if (waystone.getGoldBlock().equals(event.getClickedBlock().getLocation())) {
                        if (!playerUUID.equals(waystone.getOwner()) && !waystone.isActivated()) {
                            event.getPlayer().sendMessage("Apenas o dono pode ativar a Waystone.");
                            return;
                        }
                    }
                }
            }

        }
    }
}