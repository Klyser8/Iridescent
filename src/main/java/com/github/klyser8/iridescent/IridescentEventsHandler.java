package com.github.klyser8.iridescent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static com.github.klyser8.iridescent.api.ColorUtil.*;

public class IridescentEventsHandler implements Listener {

    private final Iridescent plugin;

    public IridescentEventsHandler(Iridescent plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().contains("/irid") || !plugin.getColorSupporters().contains(ColorSupporters.COMMAND)) return;
        event.setMessage(colorMessage(event.getPlayer(), event.getMessage(), plugin.isColorPermissions()));

    }

}
