package com.github.klyser8.iridescent.commands;

import com.github.klyser8.iridescent.Iridescent;
import com.github.klyser8.iridescent.file.MessageFetcher;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import static me.mattstudios.mf.base.components.MfUtil.color;

@Command("iridescent")
@Alias("irid")
public class ListCommand extends CommandBase {

    private final Iridescent plugin;
    private final MessageFetcher fetcher;
    public ListCommand(Iridescent plugin) {
        this.plugin = plugin;
        this.fetcher = new MessageFetcher(plugin);
    }

    @SubCommand("list")
    @Permission("irid.command.list")
    public void listCommand(CommandSender sender, @Optional Integer page) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.RED + "[IMPORTANT] Iridescent's commands are best used in game, as consoles do not support most colors.");
        }
        int maxPages = (int) Math.ceil(plugin.getColors().size() / 10f);
        if (page == null) page = 1;
        if (page > maxPages) page = maxPages;
        String divider = color("&7|&8=========|&b" + page + "&7/&9" + maxPages + "&8|=========&7|");
        int colorIndex = 0;
        int colorsDisplayed = 0;
        sender.sendMessage(divider);
        for (String tag : plugin.getColors().keySet()) {
            colorIndex++;
            if (colorIndex <= (page - 1) * 10) continue;
            if (colorsDisplayed == 10) break;
            String colorSq = color("&7 - ") + ChatColor.of(plugin.getColors().get(tag)) + "\u2B1B";
            String msg = color("&7 - &f" + tag.replace("<@", "&7<@&f").replace(">", "&7>"));
            sender.sendMessage(colorSq + msg);
            colorsDisplayed++;
        }
        sender.sendMessage(divider);
    }

}
