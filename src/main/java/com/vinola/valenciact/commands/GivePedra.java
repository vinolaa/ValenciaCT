package com.vinola.valenciact.commands;

import com.vinola.valenciact.customitems.PedraAzul;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GivePedra implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        ItemStack pedra = new PedraAzul().getPedraAzul();
        player.getInventory().addItem(pedra);
        return true;
    }
}
