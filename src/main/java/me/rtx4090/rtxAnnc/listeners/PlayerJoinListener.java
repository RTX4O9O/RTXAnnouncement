package me.rtx4090.rtxAnnc.listeners;

import me.rtx4090.rtxAnnc.AnncmntUtils;
import me.rtx4090.rtxAnnc.RTXAnnc;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        AnncmntUtils.simpleAnnouncement(event.getPlayer(), true);
    }
}
