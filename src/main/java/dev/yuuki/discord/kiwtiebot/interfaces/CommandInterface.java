package dev.yuuki.discord.kiwtiebot.interfaces;

import java.util.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface CommandInterface {
    void addSlashCommands(List<SlashCommandData> slashCommandRegister, HashMap<String, CommandInterface> map);

    void addButtonEvents(HashMap<String, CommandInterface> map);

    void executeSlashCommand(SlashCommandInteractionEvent event) throws Exception;

    void executeButton(ButtonInteractionEvent event, String[] parsedBtnId) throws Exception;
}