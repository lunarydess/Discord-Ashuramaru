package rip.lunarydess.ashuramaru.command.impl;

import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.interaction.SlashCommandInteraction;
import rip.lunarydess.ashuramaru.command.AbstractCommand;

import java.util.function.Consumer;

public final class PingCommand extends AbstractCommand {
    public @Override String name() {
        return "Ping";
    }

    public @Override String description() {
        return "Just a simple ping-pong response.";
    }

    public @Override Consumer<SlashCommandInteraction> interaction() {
        return interaction -> interaction.createImmediateResponder()
                .setContent("Pong!")
                .setFlags(MessageFlag.EPHEMERAL)
                .respond();
    }
}
