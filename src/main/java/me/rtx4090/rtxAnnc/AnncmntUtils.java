package me.rtx4090.rtxAnnc;

import me.rtx4090.rtxAnnc.log.PlayerLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public class AnncmntUtils {
    public static void simpleAnnouncement(Player p, boolean onJoin) {
        Component message = Component.text("You have ")
                .append(Component.text(PlayerLogger.getPlayerUnreadCount(p), NamedTextColor.RED))
                .append(Component.text(" unread announcements."))
                .append(Component.newline())
                .append(Component.text("Click here", NamedTextColor.GOLD, TextDecoration.BOLD))
                .clickEvent(ClickEvent.suggestCommand("/announcement"))
                .append(Component.text(" to view them."));

        p.sendMessage(message);
    }

    public static void fullAnnouncement(Player p) {
        Component message = Component.text("Announcements", NamedTextColor.YELLOW, TextDecoration.BOLD)
                .append(Component.newline());

        for (Announcement announcement : RTXAnnc.announcements) {
            if (!PlayerLogger.hasSeenAnnouncement(p.getUniqueId(), announcement.id)) {
                message.append(Component.text("· ", NamedTextColor.RED))
                        .append(Component.text(announcement.title).clickEvent(ClickEvent.callback(event -> reviewAnnouncements(p, announcement))))
                        .append(Component.newline());
            } else {
                message.append(Component.text("· "))
                        .append(Component.text(announcement.title).clickEvent(ClickEvent.callback(event -> reviewAnnouncements(p, announcement))))
                        .append(Component.newline());
            }

        }

        p.sendMessage(message);
    }

    public static void reviewAnnouncements(Player p, Announcement announcement) {
        Component message = Component.text(announcement.title, NamedTextColor.YELLOW, TextDecoration.BOLD)
                .append(Component.newline())
                .append(Component.text(announcement.content))
                .append(Component.newline())
                .append(Component.text("Posted on: " + announcement.createDate + " by " + announcement.publisher.getName(), NamedTextColor.GRAY))
                .append(Component.newline())
                .append(Component.text("Announcement ID: " + announcement.id, NamedTextColor.DARK_GRAY)
                        .clickEvent(ClickEvent.copyToClipboard(announcement.id.toString())));
        p.sendMessage(message);

        PlayerLogger.markAnnouncementAsSeen(p.getUniqueId().toString(), announcement.id.toString());

    }

    public static void informNewAnnouncement() {
        for (Player p : RTXAnnc.getPlugin().getServer().getOnlinePlayers()) {
            simpleAnnouncement(p, false);
        }
    }
}
