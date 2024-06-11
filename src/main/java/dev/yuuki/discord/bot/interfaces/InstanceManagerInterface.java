package dev.yuuki.discord.bot.interfaces;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.scheduling.TaskScheduler;

import java.util.ArrayList;
import java.util.EnumSet;

public interface InstanceManagerInterface {
    enum InstanceStatus {
        DOWN,
        PROCESSING,
        RUNNING
    }

    EnumSet<GatewayIntent> getIntentsSet();
    EnumSet<CacheFlag> getCacheFlagSet();
    ArrayList<ListenerAdapter> getListenerPool();

    JDA getJDA();
    InstanceStatus getStatus();
    TaskScheduler getTaskScheduler();

    void stop();
}
