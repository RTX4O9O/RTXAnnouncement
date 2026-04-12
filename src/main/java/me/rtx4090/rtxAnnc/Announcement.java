package me.rtx4090.rtxAnnc;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class Announcement {
    public String title;
    public String createDate;
    public String lastEditDate;
    public OfflinePlayer publisher;
    public String content;
    public UUID id;

    public Announcement(String title, String createDate, OfflinePlayer publisher, String content, UUID id) {
        this.title = title;
        this.createDate = createDate;
        this.lastEditDate = createDate;
        this.publisher = publisher;
        this.content = content;
        this.id = id;
    }
/*    public void broadcastAnnouncement() {
        String message = "§6[Announcement] §f" + title + "\n" +
                "§7Published by: " + publisher.getName() + " at " + createDate + "\n" +
                "§f" + content;
        Bukkit.broadcastMessage(message);

    }*/

}