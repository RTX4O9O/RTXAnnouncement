package me.rtx4090.rtxAnnc.log;

import me.rtx4090.rtxAnnc.RTXAnnc;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class PlayerLogger {
    private static File file;
    private static FileConfiguration log;

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
    }

    public static void markAnnouncementAsSeen(String playerUUID, String announcementUUID) {
        List<String> seenAnnouncements = log.getStringList(playerUUID + ".seen-announcements");
        if (!seenAnnouncements.contains(announcementUUID)) {
            seenAnnouncements.add(announcementUUID);
            log.set(playerUUID + ".seen-announcements", seenAnnouncements);
            saveLog();
        }
    }

    public static int getPlayerUnreadCount(Player p) {
        String playerUUID = p.getUniqueId().toString();
        List<String> seenAnnouncements = log.getStringList(playerUUID + ".seen-announcements");

        int totalAnnouncements = RTXAnnc.announcements.size();

        return totalAnnouncements - seenAnnouncements.size();
    }

    public static boolean hasSeenAnnouncement(UUID playerUUID, UUID announcementUUID) {
        List<String> seenAnnouncements = log.getStringList(playerUUID.toString() + ".seen-announcements");
        return seenAnnouncements.contains(announcementUUID.toString());
    }
}
