package me.rtx4090.rtxAnnc.log;

import me.rtx4090.rtxAnnc.RTXAnnc;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerLogger {
    private static File file;
    private static FileConfiguration log;
    private static HashMap<String, List<String>> seenAnnouncements = new HashMap<>();

    public static void setUp() {
        file = new File(RTXAnnc.getPlugin().getDataFolder(), "player-log.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().warning("Failed to create the player log: " + e.getMessage());
            }
        }

        log = YamlConfiguration.loadConfiguration(file);

        List<String> validAnnouncementIds = RTXAnnc.announcements.stream()
                .map(a -> a.id.toString())
                .toList();
        boolean needsSave = false;

        for (String playerUUID : log.getKeys(false)) {
            List<String> seenList = new ArrayList<>(log.getStringList(playerUUID + ".seen-announcements"));
            if (seenList.retainAll(validAnnouncementIds)) {
                log.set(playerUUID + ".seen-announcements", seenList);
                needsSave = true;
            }
            seenAnnouncements.put(playerUUID, seenList);
        }

        if (needsSave) {
            saveLog();
        }
    }

    public static FileConfiguration getLog() {
        return log;
    }

    public static void saveLog() {
        try {
            log.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().warning("Failed to save the player log: " + e.getMessage());
        }
    }

    public static void reloadLog() {
        log = YamlConfiguration.loadConfiguration(file);

        List<String> validAnnouncementIds = RTXAnnc.announcements.stream()
                .map(a -> a.id.toString())
                .toList();
        boolean needsSave = false;

        for (String playerUUID : log.getKeys(false)) {
            List<String> seenList = new ArrayList<>(log.getStringList(playerUUID + ".seen-announcements"));
            if (seenList.retainAll(validAnnouncementIds)) {
                log.set(playerUUID + ".seen-announcements", seenList);
                needsSave = true;
            }
            seenAnnouncements.put(playerUUID, seenList);
        }

        if (needsSave) {
            saveLog();
        }
    }

    public static void initializePlayerLog(Player p) {
        String playerUUID = p.getUniqueId().toString();
        if (!log.contains(playerUUID)) {
            log.set(playerUUID + ".seen-announcements", new ArrayList<String>());
            saveLog();
            reloadLog();
        }
    }


    public static void markAnnouncementAsSeen(String playerUUID, String announcementUUID) {
        if (!seenAnnouncements.get(playerUUID).contains(announcementUUID)) {
            seenAnnouncements.get(playerUUID).add(announcementUUID);
            log.set(playerUUID + ".seen-announcements", seenAnnouncements.get(playerUUID));
            saveLog();
        }
    }

    public static int getPlayerUnreadCount(Player p) {
        return RTXAnnc.announcements.size() - seenAnnouncements.get(p.getUniqueId().toString()).size();
    }

    public static boolean hasSeenAnnouncement(String playerUUID, String announcementUUID) {
        return seenAnnouncements.get(playerUUID).contains(announcementUUID);
    }
}