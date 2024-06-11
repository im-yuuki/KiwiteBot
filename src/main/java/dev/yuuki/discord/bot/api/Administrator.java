package dev.yuuki.discord.bot.api;

import dev.yuuki.discord.bot.services.BotInstanceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Administrator {
    BotInstanceManager botInstance;

    @Autowired
    public Administrator(BotInstanceManager botInstance) {
        this.botInstance = botInstance;
    }

    @PostMapping("/api/start")
    public String start() {
        botInstance.start();
        return "SUCCESS";
    }

    @PostMapping("/api/stop")
    public String stop() {
        botInstance.stop();
        return "SUCCESS";
    }
}
