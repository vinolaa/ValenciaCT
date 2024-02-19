package com.vinola.valenciact;

import com.vinola.valenciact.commands.*;
import com.vinola.valenciact.customcrafts.CristalCraft;
import com.vinola.valenciact.customcrafts.WaystoneCraft;
import com.vinola.valenciact.listeners.*;
import com.vinola.valenciact.storage.CristalDataHandler;
import com.vinola.valenciact.storage.Database;
import com.vinola.valenciact.storage.PlayerWaystoneDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class ValenciaCT extends JavaPlugin {

    private static ValenciaCT instance;
    private Database db;

    private void regCrafts(){
        new CristalCraft();
        new WaystoneCraft();
    }

    private void regListeners(){
        Bukkit.getPluginManager().registerEvents(new WaystonePlaceListener(), this);
        Bukkit.getPluginManager().registerEvents(new GUIListener(), this);
        Bukkit.getPluginManager().registerEvents(new CristalOpenGui(), this);
        Bukkit.getPluginManager().registerEvents(new ActivateWaystone(), this);
        Bukkit.getPluginManager().registerEvents(new StructureBreak(), this);
    }

    private void regCmds(){
        Objects.requireNonNull(getCommand("givep")).setExecutor(new GivePepita());
        Objects.requireNonNull(getCommand("givec")).setExecutor(new GiveCristal());
        Objects.requireNonNull(getCommand("givew")).setExecutor(new GiveWaystone());
        Objects.requireNonNull(getCommand("giver")).setExecutor(new GivePickaxe());
        Objects.requireNonNull(getCommand("givepedra")).setExecutor(new GivePedra());
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        db = Database.getInstance();
        PlayerWaystoneDataHandler.getInstance().loadDataFromSQL();
        CristalDataHandler.getInstance().loadDataFromSQL();
        Bukkit.getConsoleSender().sendMessage("§aValenciaCT has been enabled!");
        regCmds();
        regListeners();
        regCrafts();
    }

    @Override
    public void onDisable() {
        PlayerWaystoneDataHandler.getInstance().saveDataToSQL();
        PlayerWaystoneDataHandler.getInstance().deleteTrashFromSQL();
        CristalDataHandler.getInstance().saveDataToSQL();
        Bukkit.getConsoleSender().sendMessage("§cValenciaCT has been disabled!");
    }

    public static ValenciaCT getInstance() {
        return instance;
    }

    public Database getDb() {
        return db;
    }

}
