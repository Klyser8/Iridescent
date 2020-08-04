package com.github.klyser8.iridescent.file;

import com.github.klyser8.iridescent.Iridescent;

import java.util.Objects;

import static me.mattstudios.mf.base.components.MfUtil.color;

public class MessageFetcher {

    private final Iridescent plugin;
    public MessageFetcher(Iridescent plugin) {
        this.plugin = plugin;
    }

    public String getMessageString(Message message) {
        String msg;
        if (plugin.getFileHandler().getLang().isSet(message.name()))
            msg = color(Objects.requireNonNull(plugin.getFileHandler().getLang().getString(message.name())));
        else
            msg = color(message.getDefaultMessage());
        return msg;
    }

}
