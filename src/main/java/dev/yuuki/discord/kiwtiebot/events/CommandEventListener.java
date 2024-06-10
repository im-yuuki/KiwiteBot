package dev.yuuki.discord.kiwtiebot.events;

import dev.yuuki.discord.kiwtiebot.interfaces.CommandInterface;
import dev.yuuki.discord.kiwtiebot.utils.RuntimeManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class CommandEventListener extends ListenerAdapter {
    final static Logger logger = LoggerFactory.getLogger(CommandEventListener.class);
    ArrayList<SlashCommandData> slashCmdsRegister = new ArrayList<>();
    HashMap<String, CommandInterface> slashCommandMap = new HashMap<>();
    HashMap<String, CommandInterface> buttonInteractionMap = new HashMap<>();

    RuntimeManager runtimeManager;


    @Autowired
    public CommandEventListener(RuntimeManager runtimeManager) {
        this.runtimeManager = runtimeManager;
    }

    
    public void addCommandHandler(CommandInterface cmdInterface) {
        cmdInterface.addSlashCommands(slashCmdsRegister, slashCommandMap);
        cmdInterface.addButtonEvents(buttonInteractionMap);
    }


    public void updateGlobalCommands(JDA jda) {
        logger.info("Registering global application commands");
        CommandListUpdateAction action = jda.updateCommands();
        for (SlashCommandData slashCommandData : slashCmdsRegister) action.addCommands(slashCommandData);
        action.queue();
    }

    @Override
    public final void onReady(ReadyEvent event) {
        SelfUser self = event.getJDA().getSelfUser();
        logger.info("Logged in as %s (ID: %s).".formatted(self.getName(), self.getId()));
        updateGlobalCommands(event.getJDA());
    }

    @Override
    public final void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild()) return;
        if (event.isWebhookMessage()) return;
        User user = event.getAuthor();
        if (user.isBot() || user.isSystem()) return;
        logger.info("Message received: From %s - Guild %d: %s".formatted(user.getEffectiveName(), event.getGuild().getIdLong(), event.getMessage().getContentRaw()));

    }


    @Override
    public final void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        User user = event.getUser();
        if (user.isBot() || user.isSystem()) return;
        logger.debug("Application command triggered: %s(%s) /%s".formatted(user.getName(), user.getId(), event.getFullCommandName()));
        try {
            slashCommandMap.get(event.getName()).executeSlashCommand(event);
        } catch (Exception e) {
            logger.warn("An error occured when processing command /%s by %s at channel %d".formatted(event.getFullCommandName(), event.getUser().getName(), event.getChannelIdLong()), e);
        }

    }

    @Override
    public final void onButtonInteraction(ButtonInteractionEvent event) {
        User user = event.getUser();
        Button button = event.getButton();
        if (user.isBot() || user.isSystem()) return;
        if (button.isDisabled() || button.getId() == null) return;
        logger.debug("Button event triggered: %s(%s) %s".formatted(user.getName(), user.getId(), button.getId()));
        String[] parsedBtnId = button.getId().split("_");
        try {
            buttonInteractionMap.get(parsedBtnId[0]).executeButton(event, parsedBtnId);
        } catch (Exception e) {
            logger.warn("An error occured when processing button event id %s by %s at channel %d".formatted(event.getButton().getId(), event.getUser().getName(), event.getChannelIdLong()), e);
        }
    }

    @Override
    public final void onException(ExceptionEvent event) {
        logger.error("An unhandled exception occured. Application is shutting down", event.getCause());
        runtimeManager.shutdown();
    }
}
