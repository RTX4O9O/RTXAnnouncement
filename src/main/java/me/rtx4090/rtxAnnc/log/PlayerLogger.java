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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerLogger {
    private static File file;
    private static FileConfiguration log;
    private static HashMap<String, Set<String>> seenAnnouncements = new HashMap<>();

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
        cleanupStaleAnnouncements();
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

    public static void saveLogAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(RTXAnnc.getPlugin(), PlayerLogger::saveLog);
    }

    public static void reloadLog() {
        log = YamlConfiguration.loadConfiguration(file);
        cleanupStaleAnnouncements();
    }

    private static void cleanupStaleAnnouncements() {
        Set<String> validAnnouncementIds = RTXAnnc.announcements.stream()
                .map(a -> a.id.toString())
                .collect(Collectors.toSet());
        boolean needsSave = false;

        for (String playerUUID : log.getKeys(false)) {
            List<String> seenList = new ArrayList<>(log.getStringList(playerUUID + ".seen-announcements"));
            if (seenList.retainAll(validAnnouncementIds)) {
                log.set(playerUUID + ".seen-announcements", seenList);
                needsSave = true;
            }
            seenAnnouncements.put(playerUUID, new HashSet<>(seenList));
        }

        if (needsSave) {
            saveLog();
        }
    }

    public static void initializePlayerLog(Player p) {
        String playerUUID = p.getUniqueId().toString();
        if (!log.contains(playerUUID)) {
            log.set(playerUUID + ".seen-announcements", new ArrayList<String>());
            seenAnnouncements.put(playerUUID, new HashSet<>());
            saveLogAsync();
        }
    }


    public static void markAnnouncementAsSeen(String playerUUID, String announcementUUID) {
        Set<String> seen = seenAnnouncements.get(playerUUID);
        if (seen != null && seen.add(announcementUUID)) {
            log.set(playerUUID + ".seen-announcements", new ArrayList<>(seen));
            saveLogAsync();
        }
    }

    public static int getPlayerUnreadCount(Player p) {
        Set<String> seen = seenAnnouncements.get(p.getUniqueId().toString());
        return RTXAnnc.announcements.size() - (seen != null ? seen.size() : 0);
    }

    public static boolean hasSeenAnnouncement(String playerUUID, String announcementUUID) {
        Set<String> seen = seenAnnouncements.get(playerUUID);
        return seen != null && seen.contains(announcementUUID);
    }
}