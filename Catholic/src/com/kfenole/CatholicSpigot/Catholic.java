package com.kfenole.CatholicSpigot;

import org.bukkit.plugin.java.JavaPlugin;

public class Catholic extends JavaPlugin {

    @Override
    public void onEnable(){
        //Fired when the server enables the plugin
        // Register our command "kit" (set an instance of your command class as executor)
        this.getCommand("sod").setExecutor(new CommandSOD());

    }

    @Override
    public void onDisable(){
        //Fired when the server stops and disables all plugins

    }

}

