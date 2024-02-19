package com.vinola.valenciact.commands;

import com.vinola.valenciact.customitems.PepitaCristal;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GivePepita implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        ItemStack pepita = new PepitaCristal().getPepCristal();
        player.getInventory().addItem(pepita);
        return true;
    }
}
