package rip.lunarydess.ashuramaru.command;

import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.function.Consumer;

public abstract class AbstractCommand implements ICommand {
    public abstract String name();
    public abstract String description();
    public abstract Consumer<SlashCommandInteraction> interaction();
}
