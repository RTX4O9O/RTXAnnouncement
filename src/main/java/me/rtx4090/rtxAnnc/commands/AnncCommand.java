package me.rtx4090.rtxAnnc.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.rtx4090.rtxAnnc.AnncmntUtils;
import me.rtx4090.rtxAnnc.Announcement;
import me.rtx4090.rtxAnnc.RTXAnnc;
import me.rtx4090.rtxAnnc.log.AnncmntLogger;
import me.rtx4090.rtxAnnc.log.PlayerLogger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;

public class AnncCommand implements BasicCommand {
    private Announcement announcement;

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        if (!commandSourceStack.getSender().hasPermission("rtxannouncement.op")) {
            handleRead(commandSourceStack);
            return;
        }

        if (args.length == 0) {
            handleRead(commandSourceStack);
            return;
        }

        switch (args[0]) {
            case "create":
                handleCreate(commandSourceStack, args);
                break;

            case "delete":
                handleDelete(commandSourceStack, args);
                break;

            case "edit":
                handleEdit(commandSourceStack, args);
                break;

            case "reload":
                handleReload(commandSourceStack);
                break;

            default:
                handleRead(commandSourceStack);
                break;
        }


    }

    @Override
    public Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (!stack.getSender().hasPermission("rtxannouncement.op")) return List.of();

        if (args.length == 1) {
            return List.of("create", "delete", "edit", "reload");
        }

        return List.of();
    }

    private void handleRead(CommandSourceStack stack) {
        CommandSender sender = stack.getSender();

        if (sender instanceof Player) {
            Player p = (Player) sender;
            AnncmntUtils.fullAnnouncement(p);

        } else {
            sender.sendMessage("This is a player only command.");
        }
    }

    private void handleCreate(CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();

        if (args.length < 3) {
            sender.sendMessage("Usage: /announce create <title> <content>");
            return;
        }
        args = Arrays.copyOfRange(args, 1, args.length);
        String arguments = String.join(" ", args);
        arguments = arguments.substring(0, arguments.length() - 1);
        String title = arguments.split("\" \"")[0].replace("\"", "");
        String content = arguments.split("\" \"")[1].replace("\"", "");;
        Date now = new Date();
        String date = Instant.ofEpochMilli(now.getTime()).toString(); // date in ISO 8601 format
        UUID id = UUID.randomUUID();

        if (sender instanceof Player) {
            Player p = (Player) sender;
            announcement = new Announcement(title, date, Bukkit.getOfflinePlayer(p.getUniqueId()), content, id);

        } else {
            announcement = new Announcement(title, date, Bukkit.getOfflinePlayer("f78a4d8d-d51b-4b39-98a3-230f2de0c670"), content, id);
        }

        AnncmntLogger.saveAnnouncement(announcement);
        RTXAnnc.announcements.add(announcement);
        sender.sendMessage("Announcement created successfully with ID: " + announcement.id);

        AnncmntUtils.informNewAnnouncement();
    }

    private void handleDelete(CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (args.length != 2) {
            sender.sendMessage("Usage: /announce delete <uuid>");
            return;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(args[1]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("Invalid UUID format.");
            return;
        }

        announcement = AnncmntLogger.getAnnouncement(uuid);
        if (announcement != null) {
            AnncmntLogger.getLog().set(uuid.toString(), null);
            AnncmntLogger.saveLog();
            AnncmntLogger.reloadLog();
            PlayerLogger.reloadLog();
            RTXAnnc.announcements.removeIf(a -> a.id.equals(uuid));
            sender.sendMessage("Announcement deleted successfully.");
        } else {
            sender.sendMessage("Announcement not found.");
        }
    }

    private void handleEdit(CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        sender.sendMessage("This function is under development. Try edit it in the announcement log file and reload manually for now.");
    }

    private void handleReload(CommandSourceStack stack) {
        CommandSender sender = stack.getSender();
        AnncmntLogger.reloadLog();
        PlayerLogger.reloadLog();
        RTXAnnc.announcements = AnncmntLogger.getAllAnnouncements();
        sender.sendMessage("Announcement log reloaded successfully.");
    }
}
