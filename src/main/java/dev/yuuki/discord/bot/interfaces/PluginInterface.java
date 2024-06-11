package dev.yuuki.discord.bot.interfaces;

public interface PluginInterface {
    String name();
    String version();

    void load(InstanceManagerInterface botInstance);
    void stop();
}
