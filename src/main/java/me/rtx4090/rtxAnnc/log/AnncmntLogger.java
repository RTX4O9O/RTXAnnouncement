package me.rtx4090.rtxAnnc.log;

import me.rtx4090.rtxAnnc.Announcement;
import me.rtx4090.rtxAnnc.RTXAnnc;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class AnncmntLogger {
    private static File file;
    private static FileConfiguration log;

    public static void setUp() {
        file = new File(RTXAnnc.getPlugin().getDataFolder(), "announcement-log.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().warning("Failed to create the announcement log: " + e.getMessage());
            }
        }

        log = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration getLog() {
        return log;
    }

    public static void saveLog() {
        try {
            log.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().warning("Failed to save the announcement log: " + e.getMessage());
        }
    }

    public static void reloadLog() {
        log = YamlConfiguration.loadConfiguration(file);
    }

    public static ArrayList<Announcement> getAllAnnouncements() {
        ArrayList<Announcement> announcements = new ArrayList<>();
        for (String key : log.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);
                Announcement announcement = getAnnouncement(id);
                if (announcement != null) {
                    announcements.add(announcement);
                }
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("Invalid UUID key in log: " + key);
            }
        }
        return announcements;
    }

    public static Announcement getAnnouncement(UUID id) {
        String path = id.toString();
        if (log.contains(path)) {
            String title = log.getString(path + ".title");
            String createDate = log.getString(path + ".create-date");
            String lastEditDate = log.getString(path + ".last-edit-date");
            UUID publisherId = UUID.fromString(log.getString(path + ".publisher"));
            String content = log.getString(path + ".content");
            return new Announcement(title, createDate, RTXAnnc.getPlugin().getServer().getOfflinePlayer(publisherId), content, id);

        } else {
            Bukkit.getLogger().warning("Announcement with ID " + id + " not found in the log.");
            return null;
        }

    }

    public static void saveAnnouncement(Announcement annc) {
        String path = annc.id.toString();
        log.set(path + ".title", annc.title);
        log.set(path + ".create-date", annc.createDate);
        log.set(path + ".last-edit-date", annc.lastEditDate);
        log.set(path + ".publisher", annc.publisher.getUniqueId().toString());
        log.set(path + ".content", annc.content);
        saveLog();
    }


}
