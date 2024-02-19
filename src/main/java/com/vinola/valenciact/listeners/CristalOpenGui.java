package com.vinola.valenciact.listeners;

import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI;
import com.github.fierioziy.particlenativeapi.core.ParticleNativeCore;
import com.vinola.valenciact.ValenciaCT;
import com.vinola.valenciact.guis.NoWaystoneGUI;
import com.vinola.valenciact.guis.WaystoneGUI;
import com.vinola.valenciact.storage.CristalDataHandler;
import com.vinola.valenciact.storage.PlayerWaystoneDataHandler;
import com.vinola.valenciact.structures.WaystoneStructure;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class CristalOpenGui implements Listener {
    PlayerWaystoneDataHandler playerWaystoneDataHandler = PlayerWaystoneDataHandler.getInstance();

    private void teleportEffect(Location location, Player player) {
        ParticleNativeAPI particleApi = ParticleNativeCore.loadAPI(ValenciaCT.getInstance());
        particleApi.LIST_1_13.SOUL_FIRE_FLAME
                .packet(true, location)
                .sendTo(player);
    }

    private void aftterTp(Location location, Player player) {
        ParticleNativeAPI particleApi = ParticleNativeCore.loadAPI(ValenciaCT.getInstance());
        particleApi.LIST_1_13.EXPLOSION
                .packet(true, location)
                .sendTo(player);
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if (!event.getAction().toString().contains("RIGHT")) return;

        Player p = event.getPlayer();
        ItemStack itemInHand = p.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.PAPER) {
            ItemMeta itemMeta = itemInHand.getItemMeta();
            if (itemMeta != null && itemMeta.getDisplayName().contains("Cristal de teleporte")) {
                Player player = event.getPlayer();
                if (playerWaystoneDataHandler.getPlayerWaystones(player.getUniqueId()) == null || playerWaystoneDataHandler.getPlayerWaystones(player.getUniqueId()).isEmpty()) {
                    NoWaystoneGUI noWaystoneGUI = new NoWaystoneGUI();
                    player.openInventory(noWaystoneGUI.createGUI());
                } else {
                    WaystoneGUI waystoneGUI = new WaystoneGUI();
                    player.openInventory(waystoneGUI.createGUI(event.getPlayer().getUniqueId()));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerLeftClick(PlayerInteractEvent event) {
        if (!event.getAction().toString().contains("LEFT")) return;

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = itemInHand.getItemMeta();

        if (itemInHand.getType() == Material.PAPER && itemMeta != null && itemMeta.getDisplayName().contains("Cristal de teleporte")) {
            List<WaystoneStructure> playerWaystones = playerWaystoneDataHandler.getPlayerWaystones(player.getUniqueId());
            if (playerWaystones == null || playerWaystones.isEmpty()) {
                player.sendMessage("§cVocê não tem totens de teleporte para usar!");
            } else {
                List<WaystoneStructure> activeWaystones = playerWaystones.stream().filter(WaystoneStructure::isActivated).collect(Collectors.toList());
                if (activeWaystones.isEmpty()) {
                    player.sendMessage("§cVocê não tem totens de teleporte ativas para se teleportar!");
                } else {
                    Random random = new Random();
                    WaystoneStructure randomWaystone = activeWaystones.get(random.nextInt(activeWaystones.size()));
                    Location teleportLocation = randomWaystone.getLocation().clone();
                    teleportLocation.setY(teleportLocation.getY() + 1);
                    float yaw = randomWaystone.getYaw();
                    teleportLocation.setYaw(yaw);

                    if (yaw == 0) {
                        teleportLocation.setZ(teleportLocation.getZ() + 1.5);
                        teleportLocation.setX(teleportLocation.getX() + 0.5);
                    } else if (yaw == 90) {
                        teleportLocation.setX(teleportLocation.getX() - 0.5);
                        teleportLocation.setZ(teleportLocation.getZ() + 0.5);
                    } else if (yaw == -90) {
                        teleportLocation.setX(teleportLocation.getX() + 1.5);
                        teleportLocation.setZ(teleportLocation.getZ() + 0.5);
                    } else if (yaw == 180) {
                        teleportLocation.setX(teleportLocation.getX() + 0.5);
                        teleportLocation.setZ(teleportLocation.getZ() - 0.5);
                    }

                    if (itemInHand.getType() == Material.PAPER && itemMeta.getDisplayName().contains("Cristal de teleporte")) {
                        NamespacedKey key = new NamespacedKey(ValenciaCT.getInstance(), "cristalUUID");
                        String cristalUUIDString = itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                        assert cristalUUIDString != null;
                        UUID cristalUUID = UUID.fromString(cristalUUIDString);
                        CristalDataHandler cdh = CristalDataHandler.getInstance();
                        if (cdh.getCristalUses().containsKey(cristalUUID)) {
                            if (ValenciaCT.teleportingPlayers.contains(player)) {
                                player.sendMessage("§cVocê já está se teleportando, agurade.");
                                return;
                            }
                            player.sendMessage("§aTeleportado para o totem de teleporte em 2 segundos...");
                            player.closeInventory();
                            ValenciaCT.teleportingPlayers.add(player);

                            BukkitTask particleTask = Bukkit.getScheduler().runTaskTimer(ValenciaCT.getInstance(), () -> {
                                for (int i = 0; i < 360; i += 15) {
                                    double angle = i * Math.PI / 180;
                                    double x = 1.5 * Math.cos(angle);
                                    double z = 1.5 * Math.sin(angle);
                                    Location particleLocation = player.getLocation().add(x, 1.2, z);
                                    teleportEffect(particleLocation, player);
                                }
                            }, 0L, 13L);

                            player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1.0f, 1.0f);
                            Bukkit.getScheduler().runTaskLater(ValenciaCT.getInstance(), () -> {
                                player.teleport(teleportLocation);
                                particleTask.cancel();
                                player.stopSound(Sound.BLOCK_PORTAL_TRAVEL, SoundCategory.PLAYERS);
                                player.playSound(player.getLocation(), Sound.BLOCK_GRASS_PLACE, 1.0f, 1.0f);
                                player.sendMessage("§aTeleportado para o totem com sucesso!");
                                cdh.decrementUses(cristalUUID, player);
                                player.stopSound(Sound.BLOCK_GRASS_PLACE, SoundCategory.PLAYERS);

                                Location particleLocation = player.getLocation().add(player.getLocation().getDirection().multiply(2));
                                aftterTp(particleLocation, player);
                                ValenciaCT.teleportingPlayers.remove(player);
                            }, 40L);
                        } else {
                            player.sendMessage("§cEste cristal de teleporte não é válido.");
                        }
                    }
                }
            }
        }
    }

}