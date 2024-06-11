package dev.yuuki.discord.kiwtie.interfaces;

import dev.yuuki.discord.bot.interfaces.InstanceManagerInterface;

public interface PluginInterface {
    String name = null;
    String version = null;

    void load(InstanceManagerInterface botInstance);
    void stop();
}
