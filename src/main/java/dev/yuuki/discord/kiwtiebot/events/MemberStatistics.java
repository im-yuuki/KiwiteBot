package dev.yuuki.discord.kiwtiebot.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberStatistics extends ListenerAdapter {
    final static Logger logger = LoggerFactory.getLogger(MemberStatistics.class);
    JDA jda;

    @Value("${guild.id}") String guildId;
    @Value("${guild.statistics.online_vc_id}") String guildOnlineChannelId;
    @Value("${guild.statistics.idle_vc_id}") String guildIdleChannelId;
    @Value("${guild.statistics.dnd_vc_id}") String guildDndChannelId;

    boolean running = false;
    int total = 0, online = 0, idle = 0, dnd = 0;


    @Autowired
    public MemberStatistics(JDA jda) {
        this.jda = jda;
    }


    @Scheduled(fixedRate = 60000)
    public void updateStatisticsChannel() {
        if (running) return;
        running = true;
        logger.info("Refreshing server's member statistics board");
        try {
            int total = 0, online = 0, idle = 0, dnd = 0;
            Guild guild = jda.getGuildById(guildId);
            if (guild == null) throw new AssertionError("Cannot get guild by ID");
            List<Member> guildMembers = guild.getMembers();
            for (Member member : guildMembers) {
                if (member.getUser().isBot()) continue;
                total++;
                switch (member.getOnlineStatus()) {
                    case OnlineStatus.ONLINE -> online++;
                    case OnlineStatus.IDLE -> idle++;
                    case OnlineStatus.DO_NOT_DISTURB -> dnd++;
                }
            }
            if (total != this.total || online != this.online) {
                guild.getVoiceChannelById(guildOnlineChannelId).getManager().setName("🟢・online: %d / %d".formatted(online, total)).queue();
                this.total = total;
                this.online = online;
            }
            if (idle != this.idle) {
                guild.getVoiceChannelById(guildIdleChannelId).getManager().setName("🌙・idle: %d".formatted(idle)).queue();
                this.idle = idle;
            }
            if (dnd != this.dnd) {
                guild.getVoiceChannelById(guildDndChannelId).getManager().setName("⛔・dnd: %d".formatted(dnd)).queue();
                this.dnd = dnd;
            }
            logger.info("Refreshed server's statistics board: online %d/%d idle %d dnd %d".formatted(online, total, idle, dnd));
        } catch (Exception e) {
            logger.error("Failed to refresh server's statistics board", e);
        } finally {
            running = false;
        }
    }

}
