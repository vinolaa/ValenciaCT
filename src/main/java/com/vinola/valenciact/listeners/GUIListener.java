package com.vinola.valenciact.listeners;

import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI;
import com.github.fierioziy.particlenativeapi.core.ParticleNativeCore;
import com.vinola.valenciact.ValenciaCT;
import com.vinola.valenciact.customitems.Cristal;
import com.vinola.valenciact.storage.CristalDataHandler;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GUIListener implements Listener {

    private void teleportEffect(Location location, Player player) {
        ParticleNativeAPI particleApi = ParticleNativeCore.loadAPI(ValenciaCT.getInstance());
        particleApi.LIST_1_13.FLAME
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
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Sem waystones") || event.getView().getTitle().equals("Waystones")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick2(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Waystones")) return;
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null && clickedItem.getType() == Material.GOLD_INGOT) {
            ItemMeta meta = clickedItem.getItemMeta();
            if (meta != null && meta.hasLore()) {
                List<String> lore = meta.getLore();
                if (lore != null && lore.size() >= 3) {
                    String locationString = lore.get(2);
                    String[] coords = locationString.split(" ");
                    if (coords.length == 8) {
                        String w = coords[1];
                        int x = Integer.parseInt(coords[3]);
                        int y = Integer.parseInt(coords[5]);
                        int z = Integer.parseInt(coords[7]);
                        Location location = new Location(Bukkit.getWorld(w), x, y + 1, z);
                        if (meta.getItemFlags().contains(ItemFlag.HIDE_POTION_EFFECTS)){
                            location.setYaw(0);
                            location.setZ(location.getZ() + 1.5);
                            location.setX(location.getX() + 0.5);
                        }else if (meta.getItemFlags().contains(ItemFlag.HIDE_DESTROYS)) {
                            location.setYaw(90);
                            location.setX(location.getX() - 0.5);
                            location.setZ(location.getZ() + 0.5);
                        }else if (meta.getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                            location.setYaw(-90);
                            location.setX(location.getX() + 1.5);
                            location.setZ(location.getZ() + 0.5);
                        }else if (meta.getItemFlags().contains(ItemFlag.HIDE_UNBREAKABLE)){
                            location.setYaw(180);
                            location.setX(location.getX() + 0.5);
                            location.setZ(location.getZ() - 0.5);
                        }
                        Player player = (Player) event.getWhoClicked();
                        ItemStack itemInHand = player.getInventory().getItemInMainHand();
                        ItemMeta itemMeta = itemInHand.getItemMeta();

                        if (itemInHand.getType() == Material.DIAMOND && itemMeta != null && itemMeta.getDisplayName().contains("Cristal de teleporte")) {
                            NamespacedKey key = new NamespacedKey(ValenciaCT.getInstance(), "cristalUUID");
                            String cristalUUIDString = itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                            assert cristalUUIDString != null;
                            UUID cristalUUID = UUID.fromString(cristalUUIDString);
                            CristalDataHandler cdh = CristalDataHandler.getInstance();
                            if (cdh.getCristalUses().containsKey(cristalUUID)) {
                                player.sendMessage("§aTeleportado para a waystone em 2 segundos...");
                                player.closeInventory();

                                BukkitTask particleTask2 = Bukkit.getScheduler().runTaskTimer(ValenciaCT.getInstance(), () -> {
                                    for (int i = 0; i < 360; i += 15) {
                                        double angle = i * Math.PI / 180;
                                        double x2 = 1.5 * Math.cos(angle);
                                        double z2 = 1.5 * Math.sin(angle);
                                        Location particleLocation = player.getLocation().add(x2, 1.2, z2);
                                        teleportEffect(particleLocation, player);
                                    }
                                }, 0L, 13L);

                                player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1.0f, 1.0f);
                                Bukkit.getScheduler().runTaskLater(ValenciaCT.getInstance(), () -> {
                                    player.teleport(location);
                                    particleTask2.cancel();
                                    player.stopSound(Sound.BLOCK_PORTAL_TRAVEL, SoundCategory.PLAYERS);
                                    player.playSound(player.getLocation(), Sound.BLOCK_GRASS_PLACE, 1.0f, 1.0f);
                                    player.sendMessage("§aTeleportado para a waystone.");
                                    cdh.decrementUses(cristalUUID, player);
                                    player.stopSound(Sound.BLOCK_GRASS_PLACE, SoundCategory.PLAYERS);
                                    Location particleLocation2 = player.getLocation().add(player.getLocation().getDirection().multiply(2));
                                    aftterTp(particleLocation2, player);
                                }, 40L);
                            } else {
                                player.sendMessage("§cEste cristal de teleporte não é válido.");
                            }
                        }
                    }
                }else{
                    Player player = (Player) event.getWhoClicked();
                    player.sendMessage("§cVocê deve ativar a waystone primeiro.");
                }
            }
        }
    }
}