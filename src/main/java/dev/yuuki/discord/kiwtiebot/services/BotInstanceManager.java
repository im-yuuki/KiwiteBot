package dev.yuuki.discord.kiwtiebot.services;

import dev.yuuki.discord.kiwtiebot.interfaces.PluginInterface;
import dev.yuuki.discord.kiwtiebot.interfaces.InstanceManagerInterface;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;


@Service
public class BotInstanceManager implements InstanceManagerInterface {
    public EnumSet<GatewayIntent> intents = EnumSet.noneOf(GatewayIntent.class);
    public EnumSet<CacheFlag> cacheFlag = EnumSet.noneOf(CacheFlag.class);
    public ArrayList<ListenerAdapter> listeners = new ArrayList<>();

    public HashMap<String, PluginInterface> pluginPool = new HashMap<>();

    final static Logger logger = LoggerFactory.getLogger(BotInstanceManager.class);

    public JDA jda = null;
    public boolean started = false;


    @Value("${discord.token}") String token;

    ApplicationContext appContext;
    TaskScheduler taskScheduler;

    @Autowired
    public BotInstanceManager(ApplicationContext appContext, TaskScheduler taskScheduler) {
        this.appContext = appContext;
        this.taskScheduler = taskScheduler;
    }


    public void loadPlugin(Class<PluginInterface>[] pluginList) {
        logger.info("Loading %d plugins".formatted(pluginList.length));
        int success = 0;
        for (Class<PluginInterface> plugin : pluginList) {
            try {
                PluginInterface instance = plugin.getDeclaredConstructor().newInstance();
                instance.load(this);
                pluginPool.put(instance.name, instance);
                success++;
            } catch (Exception e) {
                logger.error("Load plugin %s failed".formatted(plugin.getName()));
            }
        }
        logger.info("%d plugin(s) loaded".formatted(success));
    }


    public void start() {
        if (started) stop();

        logger.info("Building JDA instance");
        JDABuilder builder = JDABuilder.createLight(token);
        builder.setAutoReconnect(true);
        builder.setEnabledIntents(intents);
        builder.enableCache(cacheFlag);
        builder.setMemberCachePolicy(MemberCachePolicy.NONE);

        for (ListenerAdapter listener : listeners) builder.addEventListeners(listener);

        jda = builder.build();
        logger.info("JDA build success");
        try {
            jda.awaitReady();
            SelfUser selfUser = jda.getSelfUser();
            logger.info("Logged in as %s (ID: %s)".formatted(selfUser.getName(), selfUser.getId()));
            started = true;
        } catch (Exception e) {
            logger.error("JDA instance failed to start", e);
            stop();
        }
    }


    public void stop() {
        logger.info("Unloading plugin");

        for (PluginInterface plugin : pluginPool.values()) {
            try {
                plugin.stop();
            } catch (Exception ignored) {}
        }
        intents = EnumSet.noneOf(GatewayIntent.class);
        cacheFlag = EnumSet.noneOf(CacheFlag.class);
        listeners = new ArrayList<>();
        pluginPool = new HashMap<>();

        logger.info("Shutting down JDA");
        if (jda != null) {
            jda.shutdownNow();
            jda = null;
        }
        started = false;

        logger.info("Instance sucessfully cleaned");
    }


    @EventListener
    public void onShutdown(ContextClosedEvent ignoredEvent) {
        stop();
    }

}