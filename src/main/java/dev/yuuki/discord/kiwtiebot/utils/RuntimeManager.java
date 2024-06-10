package dev.yuuki.discord.kiwtiebot.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class RuntimeManager {
    ApplicationContext appContext;


    @Autowired
    public RuntimeManager(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    public void shutdown() {
        SpringApplication.exit(appContext, () -> 0);
    }

}