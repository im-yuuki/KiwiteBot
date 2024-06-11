package dev.yuuki.discord.kiwtiebot.interfaces;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.ArrayList;
import java.util.EnumSet;

public interface InstanceManagerInterface {
    EnumSet<GatewayIntent> intents = null;
    EnumSet<CacheFlag> cacheFlag = null;
    ArrayList<ListenerAdapter> listener = null;

    JDA jda = null;
    boolean started = false;

    void stop();
}
