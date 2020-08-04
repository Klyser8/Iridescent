package com.github.klyser8.iridescent.commands;

import com.github.klyser8.iridescent.Iridescent;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("iridescent")
@Alias("irid")
public class ReloadCommand extends CommandBase {

    private final Iridescent plugin;
    public ReloadCommand(Iridescent plugin) {
        this.plugin = plugin;
    }

    @SubCommand("reload")
    @Permission("irid.command.reload")
    public void reloadCommand(CommandSender sender) {
        plugin.setupPreferences(true);
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.of("#ff0000") + "C" +
                    ChatColor.of("#ff5e00") + "o" +
                    ChatColor.of("#ff9500") + "n" +
                    ChatColor.of("#ffcc00") + "f" +
                    ChatColor.of("#e6e217") + "i" +
                    ChatColor.of("#c3e617") + "g" +
                    ChatColor.of("#90e617") + "u" +
                    ChatColor.of("#4ee617") + "r" +
                    ChatColor.of("#00ffbf") + "a" +
                    ChatColor.of("#00fff2") + "t" +
                    ChatColor.of("#00d5ff") + "i" +
                    ChatColor.of("#0099ff") + "o" +
                    ChatColor.of("#0051ff") + "n" +
                    ChatColor.of("#ffffff") + " " +
                    ChatColor.of("#0008ff") + "r" +
                    ChatColor.of("#4c00ff") + "e" +
                    ChatColor.of("#8c00ff") + "l" +
                    ChatColor.of("#d900ff") + "o" +
                    ChatColor.of("#ff00ee") + "a" +
                    ChatColor.of("#ff00aa") + "d" +
                    ChatColor.of("#ff006f") + "e" +
                    ChatColor.of("#ff0000") + "d" +
                    ChatColor.of("#ff9500") + "!");
            ((Player) sender).playSound(((Player) sender).getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
        } else {
            sender.sendMessage(ChatColor.GREEN + "Configuration Reloaded!");
        }
    }
}
