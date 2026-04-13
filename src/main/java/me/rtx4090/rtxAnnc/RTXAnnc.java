package me.rtx4090.rtxAnnc;

import me.rtx4090.rtxAnnc.commands.AnncCommand;

import me.rtx4090.rtxAnnc.listeners.PlayerJoinListener;
import me.rtx4090.rtxAnnc.log.AnncmntLogger;
import me.rtx4090.rtxAnnc.log.PlayerLogger;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RTXAnnc extends JavaPlugin implements Listener {
    private static RTXAnnc plugin;
    public static ArrayList<Announcement> announcements = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("The plugin has been enabled!");
        plugin = this;

        //Listener Registration
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        //Command Registration
        registerCommand("announcement", "Commands for RTX Announcement",
                List.of("annc", "anncmnt", "announce"), new AnncCommand());

        //AnncLog Setup
        AnncmntLogger.setUp();
        AnncmntLogger.getLog().options().copyDefaults(true);
        AnncmntLogger.saveLog();
        announcements = AnncmntLogger.getAllAnnouncements();
        Bukkit.getLogger().info("Loaded " + announcements.size() + " announcements from the log.");

        //PlayerLog Setup
        PlayerLogger.setUp();
        PlayerLogger.getLog().options().copyDefaults(true);
        PlayerLogger.saveLog();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        AnncmntLogger.saveLog();
        PlayerLogger.saveLog();
        Bukkit.getLogger().info("The plugin has been disabled!");
    }

    public static RTXAnnc getPlugin() {
        return plugin;
    }



}
