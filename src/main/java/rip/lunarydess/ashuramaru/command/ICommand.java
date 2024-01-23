package rip.lunarydess.ashuramaru.command;

import org.javacord.api.interaction.SlashCommandOptionBuilder;

public interface ICommand {
    default SlashCommandOptionBuilder[] options() {
        return new SlashCommandOptionBuilder[0];
    }
}
