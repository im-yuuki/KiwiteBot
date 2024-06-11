package dev.yuuki.discord.kiwtiebot.interfaces;

public interface PluginInterface {
    String name = null;
    String version = null;

    void load(InstanceManagerInterface botInstance);
    void stop();
}
