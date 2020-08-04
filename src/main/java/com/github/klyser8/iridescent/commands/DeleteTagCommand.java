package com.github.klyser8.iridescent.commands;

import com.github.klyser8.iridescent.Iridescent;
import com.github.klyser8.iridescent.file.Message;
import com.github.klyser8.iridescent.file.MessageFetcher;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("iridescent")
@Alias("irid")
public class DeleteTagCommand extends CommandBase {

    private final Iridescent plugin;
    private final MessageFetcher fetcher;
    public DeleteTagCommand(Iridescent plugin) {
        this.plugin = plugin;
        this.fetcher = new MessageFetcher(plugin);
    }

    @SubCommand("delete")
    @Permission("irid.command.delete")
    public void deleteCommand(CommandSender sender, @Optional String tagName) {
        boolean error = false;
        String tagRegex = "^[A-Za-z0-9]{1,13}$";
        if (!plugin.getColors().containsKey(tagName)) {
            sender.sendMessage(fetcher.getMessageString(Message.ERROR_MISSING_TAG));
            error = true;
        }
        if (!error) {
            String rawTag = deleteColorTag(tagName);
            sender.sendMessage(fetcher.getMessageString(Message.TAG_DELETION).replace("<tag>", ChatColor.BOLD + rawTag));
        }
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        if (error) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, SoundCategory.PLAYERS, 1.0f, 1.0f);
        } else {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, SoundCategory.PLAYERS, 1.0f, 2.0f);
        }
    }

    /**
     * Deletes an existing color tag
     *
     * @param tagName the color tag to delete
     * @return the deleted tag's name
     */
    private String deleteColorTag(String tagName) {
        plugin.getColors().remove(tagName);
        return tagName;
    }
}
