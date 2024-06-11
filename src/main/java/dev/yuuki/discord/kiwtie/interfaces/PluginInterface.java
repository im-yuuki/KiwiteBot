package dev.yuuki.discord.kiwtie.interfaces;

public interface PluginInterface {
    String name = null;
    String version = null;

    void load(InstanceManagerInterface botInstance);
    void stop();
}
