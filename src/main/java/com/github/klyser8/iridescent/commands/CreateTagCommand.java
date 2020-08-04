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
public class CreateTagCommand extends CommandBase {

    private final Iridescent plugin;
    private final MessageFetcher fetcher;
    public CreateTagCommand(Iridescent plugin) {
        this.plugin = plugin;
        this.fetcher = new MessageFetcher(plugin);
    }

    @SubCommand("create")
    @Permission("irid.command.create")
    public void createCommand(CommandSender sender, String colorHex, @Optional String tagName) {
        boolean error = false;
        if ((colorHex == null || !colorHex.matches(Iridescent.HEX_REGEX)) ||
                (tagName == null || !tagName.matches(Iridescent.TAG_REGEX) || plugin.getColors().containsKey(tagName))) {
            if (colorHex == null || !colorHex.matches(Iridescent.HEX_REGEX)) {
                sender.sendMessage(fetcher.getMessageString(Message.ERROR_INVALID_HEX));
            } else if (tagName == null || !tagName.matches(Iridescent.TAG_REGEX)) {
                sender.sendMessage(fetcher.getMessageString(Message.ERROR_INVALID_TAG));
            } else {
                sender.sendMessage(fetcher.getMessageString(Message.ERROR_EXISTING_TAG));
            }
            error = true;
        }

        if (!error) {
            String rawTag = createColorTag(colorHex, tagName);
            sender.sendMessage(fetcher.getMessageString(Message.TAG_CREATION).replace("<tag>", rawTag)
                    .replace(rawTag, ChatColor.of(plugin.getColors().get(rawTag)) + "" + ChatColor.BOLD + rawTag));
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
     * Adds a color tag to the {@link Iridescent#getColors()}, and writes it in the plugin's
     * config.yml for future usage.
     *
     * @param colorHex the hex color code for the color tag (#f0f0f0).
     * @param tagName the tag name. Can be between 1 and 13 characters long, alphanumerical.
     * @return the created tag's name
     */
    private String createColorTag(String colorHex, String tagName) {
        plugin.getColors().put(tagName, colorHex);
        return tagName;
    }
}
