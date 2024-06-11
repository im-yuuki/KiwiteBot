package dev.yuuki.discord.kiwtie;

import dev.yuuki.discord.bot.interfaces.InstanceManagerInterface;
import dev.yuuki.discord.bot.interfaces.PluginInterface;

public class Plugin implements PluginInterface {
    @Override
    public String name() { return "KiwtieBot"; }
    @Override
    public String version() { return "0.1-NIGHTLY"; }

    InstanceManagerInterface botInstance;

    @Override
    public void load(InstanceManagerInterface botInstance) {
        this.botInstance = botInstance;
    }

    @Override
    public void stop() {

    }
}
