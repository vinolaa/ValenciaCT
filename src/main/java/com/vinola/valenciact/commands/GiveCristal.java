package com.vinola.valenciact.commands;

import com.vinola.valenciact.customitems.Cristal;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GiveCristal implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player p = (Player) commandSender;
        ItemStack cristal = new Cristal(false).getCristal();
        p.getInventory().addItem(cristal);
        return true;
    }
}
