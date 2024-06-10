package dev.yuuki.discord.kiwtiebot;

import dev.yuuki.discord.kiwtiebot.events.CommandEventListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;


@Configuration
@EnableScheduling
public class AppConfiguration {
    final static Logger logger = LoggerFactory.getLogger(AppConfiguration.class);

    CommandEventListener commandEventListener;
    JDA jda;


    @Value("${discord.token}")
    String token;


    public AppConfiguration(CommandEventListener commandEventListener) {
        this.commandEventListener = commandEventListener;
    }


    @Bean
    public JDA createBotInstance() {
        JDABuilder builder = JDABuilder.createLight(token);
        builder.setEnabledIntents(
                GatewayIntent.MESSAGE_CONTENT, // Need to be enabled in Developer Portal
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MODERATION,
                GatewayIntent.GUILD_PRESENCES // Need to be enabled in Developer Portal
        );

        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.enableCache(CacheFlag.ONLINE_STATUS);

        builder.addEventListeners(commandEventListener);
        builder.setLargeThreshold(50);
        try {
            jda = builder.build().awaitReady();
            logger.info("JDA bot instance sucessfully initalized");
        } catch (InterruptedException e) {
            logger.error("JDA initalize failed.", e);
        }
        return jda;
    }


    @EventListener
    public void onShutdown(ContextClosedEvent ignoredEvent) {
        try {
            jda.shutdownNow();
        } catch (Exception ignored) {}
    }

}
