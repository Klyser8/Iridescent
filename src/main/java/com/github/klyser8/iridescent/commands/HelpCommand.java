package com.github.klyser8.iridescent.commands;

import com.github.klyser8.iridescent.Iridescent;
import com.github.klyser8.iridescent.file.Message;
import com.github.klyser8.iridescent.file.MessageFetcher;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("iridescent")
@Alias("irid")
public class HelpCommand extends CommandBase {

    private final Iridescent plugin;
    private final MessageFetcher fetcher;
    public HelpCommand(Iridescent plugin) {
        this.plugin = plugin;
        this.fetcher = new MessageFetcher(plugin);
    }

    @SubCommand("help")
    @Permission("irid.command.help")
    public void helpCommand(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_AQUA + "[/Iridescent's alias: /Irid]");
        if (sender.hasPermission("irid.command.reload")) sender.sendMessage(fetcher.getMessageString(Message.HELP_TAG_RELOAD));
        if (sender.hasPermission("irid.command.create")) sender.sendMessage(fetcher.getMessageString(Message.HELP_TAG_CREATE));
        if (sender.hasPermission("irid.command.delete")) sender.sendMessage(fetcher.getMessageString(Message.HELP_TAG_DELETE));
        if (sender.hasPermission("irid.command.list")) sender.sendMessage(fetcher.getMessageString(Message.HELP_TAG_LIST));
    }
}
