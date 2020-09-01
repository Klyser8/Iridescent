package com.github.klyser8.iridescent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.github.klyser8.iridescent.commands.*;
import com.github.klyser8.iridescent.file.FileHandler;
import com.github.klyser8.iridescent.file.Message;
import com.github.klyser8.iridescent.file.MessageFetcher;
import com.github.klyser8.iridescent.wrappers.*;
import me.mattstudios.mf.base.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import static com.github.klyser8.iridescent.api.ColorUtil.*;

public final class Iridescent extends JavaPlugin {

    public static final String HEX_REGEX = "#[A-Fa-f0-9]{6}";
    public static final String TAG_REGEX = "^[A-Za-z0-9_]{1,16}$";
    public static final String RAW_HEX_REGEX = "<#[A-Fa-f0-9]{6}>";
    private String rawTagRegex;

    private final SortedMap<String, String> colorMap;
    private final List<ColorSupporters> colorSupporters;
    private final FileHandler fileHandler;

    private String language = "english";
    private byte debugLevel = 0;
    private String format = "<@{TAG}>";
    private boolean colorPermissions = false;

    public Iridescent() {
        this.colorMap = new TreeMap<>();
        colorSupporters = new ArrayList<>();
        fileHandler = new FileHandler(this);

    }

    @Override
    public void onEnable() {
        MessageFetcher fetcher = new MessageFetcher(this);
        Bukkit.getPluginManager().registerEvents(new IridescentEventsHandler(this), this);
        CommandManager commandManager = new CommandManager(this, true);
        commandManager.getMessageHandler().register("cmd.no.permission", sender -> sender.sendMessage(fetcher.getMessageString(Message.ERROR_PERMISSION)));
        commandManager.register(new ReloadCommand(this));
        commandManager.register(new HelpCommand(this));
        commandManager.register(new CreateTagCommand(this));
        commandManager.register(new DeleteTagCommand(this));
        commandManager.register(new ListCommand(this));

        saveDefaultConfig();
        setupPreferences(false);
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        if (colorSupporters.contains(ColorSupporters.SCOREBOARD)) {
            manager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.SCOREBOARD_OBJECTIVE) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    PacketContainer packet = event.getPacket();
                    WrapperPlayServerScoreboardObjective wrapper = new WrapperPlayServerScoreboardObjective(packet);
                    WrappedChatComponent component = wrapper.getDisplayName();
                    if (component == null) return;
                    component.setJson(legacyToJson(colorMessage(event.getPlayer(), jsonToLegacy(component.getJson()), false)));
                    wrapper.setDisplayName(component);
                }
            });
        }

        if (colorSupporters.contains(ColorSupporters.SCOREBOARD)) {
            manager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.SCOREBOARD_TEAM) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    PacketContainer packet = event.getPacket();
                    WrapperPlayServerScoreboardTeam wrapper = new WrapperPlayServerScoreboardTeam(packet);

                    WrappedChatComponent component = wrapper.getDisplayName();
                    component.setJson(legacyToJson(colorMessage(event.getPlayer(), jsonToLegacy(component.getJson()), false)));
                    wrapper.setDisplayName(component);

                    component = wrapper.getPrefix();
                    component.setJson(legacyToJson(colorMessage(event.getPlayer(), jsonToLegacy(component.getJson()), false)));
                    wrapper.setPrefix(component);

                    component = wrapper.getSuffix();
                    component.setJson(legacyToJson(colorMessage(event.getPlayer(), jsonToLegacy(component.getJson()), false)));
                    wrapper.setSuffix(component);
                }
            });
        }

        if (colorSupporters.contains(ColorSupporters.TABLIST)) {
            manager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    PacketContainer packet = event.getPacket();
                    WrapperPlayServerPlayerListHeaderFooter wrapper = new WrapperPlayServerPlayerListHeaderFooter(packet);

                    WrappedChatComponent component = wrapper.getHeader();
                    component.setJson(legacyToJson(colorMessage(event.getPlayer(), jsonToLegacy(component.getJson()), false)));
                    wrapper.setHeader(component);

                    component = wrapper.getFooter();
                    component.setJson(legacyToJson(colorMessage(event.getPlayer(), jsonToLegacy(component.getJson()), false)));
                    wrapper.setFooter(component);
                }
            });
        }

        if (colorSupporters.contains(ColorSupporters.TITLE)) {
            manager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.TITLE) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    PacketContainer packet = event.getPacket();
                    WrapperPlayServerTitle wrapper = new WrapperPlayServerTitle(packet);
                    WrappedChatComponent component = wrapper.getTitle();
                    if (component == null) return;
                    component.setJson(legacyToJson(colorMessage(event.getPlayer(), jsonToLegacy(component.getJson()), false)));
                    wrapper.setTitle(component);
                }
            });
        }

        if (colorSupporters.contains(ColorSupporters.ACTIONBAR) || colorSupporters.contains(ColorSupporters.PLUGIN_MESSAGE) || colorSupporters.contains(ColorSupporters.CHAT)) {
            manager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.CHAT) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    PacketContainer packet = event.getPacket();
                    WrapperPlayServerChat wrapper = new WrapperPlayServerChat(packet);
                    WrappedChatComponent component = wrapper.getMessage();
                    if (component == null) return;
                    if (wrapper.getChatType() == EnumWrappers.ChatType.GAME_INFO && colorSupporters.contains(ColorSupporters.ACTIONBAR)) {
                        component.setJson(legacyToJson(colorMessage(event.getPlayer(), jsonToLegacy(component.getJson()), false)));
                        wrapper.setMessage(component);
                    }
                    if (wrapper.getChatType() == EnumWrappers.ChatType.CHAT && colorSupporters.contains(ColorSupporters.PLUGIN_MESSAGE)) {
                        component.setJson(legacyToJson(colorMessage(event.getPlayer(), jsonToLegacy(component.getJson()), false)));
                        wrapper.setMessage(component);
                    }

                    if (wrapper.getChatType() == EnumWrappers.ChatType.SYSTEM && colorSupporters.contains(ColorSupporters.CHAT)) {
                        component.setJson(legacyToJson(colorMessage(event.getPlayer(), jsonToLegacy(component.getJson()), true)));
                        wrapper.setMessage(component);
                    }
                }
            });
        }

        if (colorSupporters.contains(ColorSupporters.BOSSBAR)) {
            manager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.BOSS) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    PacketContainer packet = event.getPacket();
                    WrapperPlayServerBoss wrapper = new WrapperPlayServerBoss(packet);
                    WrappedChatComponent component = wrapper.getTitle();
                    component.setJson(legacyToJson(colorMessage(event.getPlayer(), jsonToLegacy(component.getJson()), false)));
                    wrapper.setTitle(component);
                }
            });
        }

        if (colorSupporters.contains(ColorSupporters.MOTD)) {
            manager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Status.Server.SERVER_INFO) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    PacketContainer packet = event.getPacket();
                    WrapperStatusServerServerInfo wrapper = new WrapperStatusServerServerInfo(packet);
                    WrappedServerPing jsonResponse = wrapper.getJsonResponse();
                    WrappedChatComponent component = jsonResponse.getMotD();
                    component.setJson(legacyToJson(colorMessage(event.getPlayer(), jsonToLegacy(component.getJson()), false)));
                    jsonResponse.setMotD(component);
                    wrapper.setJsonResponse(jsonResponse);
                }
            });
        }


        /*manager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                WrapperPlayServerPlayerInfo wrapper = new WrapperPlayServerPlayerInfo(packet);
                List<PlayerInfoData> dataList = wrapper.getData();
                System.out.println(dataList.size());
                WrappedChatComponent displayName = dataList.get(0).getDisplayName();
                displayName.setJson(coloredTextToJson(dataList.get(0).getDisplayName().getJson()));
                List<PlayerInfoData> data = Collections.singletonList(new PlayerInfoData(dataList.get(0).getProfile(), dataList.get(0).getLatency(), dataList.get(0).getGameMode(), displayName));
                wrapper.setData(data);
            }
        });*/
    }

    @Override
    public void onDisable() {
        saveTags();
    }

    public void setupPreferences(boolean isReload) {
        if (isReload) reloadConfig();
        debugLevel = (byte) getConfig().getInt("Debug");

        format = getConfig().getString("Tag Format");
        assert format != null;
        String[] splitFormat = format.split("\\{TAG}");
        if (splitFormat.length == 2)
            rawTagRegex = "^" + splitFormat[0] + "[A-Za-z0-9_]{1,16}" + splitFormat[1] + "$";
        else
            rawTagRegex = "^" + splitFormat[0] + "[A-Za-z0-9_]{1,16}$";

        if (debugLevel >= 1) getLogger().info("- [Debug Level]: " + debugLevel);
        if (isReload) {
            saveTags();
            colorMap.clear();
            colorSupporters.clear();
            reloadConfig();
        }
        fileHandler.setup();

        for (String supporter : getConfig().getStringList("Color Support")) {
            colorSupporters.add(ColorSupporters.valueOf(supporter.toUpperCase()));
        }
        if (debugLevel >= 1) getLogger().info("- [Color Supporters]: " + colorSupporters.toString());

        ConfigurationSection section = fileHandler.getColors().getConfigurationSection("Color Tags");
        if (section == null) throw new NullPointerException();
        for (String color : section.getKeys(false)) {
            String tag = section.getString(color);
            if (tag == null) continue;
            if (tag.matches(rawTagRegex)) {

               tag = tag.replace(splitFormat[0], "");
               if (splitFormat.length == 2) tag = tag.replace(splitFormat[1], "");

               section.set(color, tag);
               fileHandler.saveColors();
               getLogger().info("Color #" + color + "'s tag updated to 1.1.2's format.");
            }
            if (!tag.matches(TAG_REGEX)) {
                getLogger().severe("Could not load color #" + color + ": TAG INVALID!");
                continue;
            }
            colorMap.put(tag.toLowerCase(), "#" + color.toLowerCase());
        }
        colorPermissions = getConfig().getBoolean("Chat Permissions");
        if (debugLevel >= 1) getLogger().info("- [Loaded Colors]: " + colorMap.keySet().toString());
    }

    public void saveTags() {
        fileHandler.getColors().set("Color Tags", null);
        for (Map.Entry<String, String> entry : colorMap.entrySet()) {
            fileHandler.getColors().set("Color Tags." + entry.getValue().substring(1), entry.getKey());
        }
        fileHandler.saveColors();
    }

    public Map<String, String> getColors() {
        return colorMap;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public FileHandler getFileHandler() {
        return fileHandler;
    }

    public byte getDebugLevel() {
        return debugLevel;
    }

    public List<ColorSupporters> getColorSupporters() {
        return colorSupporters;
    }

    public boolean isColorPermissions() {
        return colorPermissions;
    }

    public String getFormat() {
        return format;
    }
}
