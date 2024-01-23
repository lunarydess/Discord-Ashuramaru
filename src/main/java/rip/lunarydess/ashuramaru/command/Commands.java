package rip.lunarydess.ashuramaru.command;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import rip.lunarydess.ashuramaru.Ashuramaru;
import rip.lunarydess.ashuramaru.command.impl.PingCommand;
import rip.lunarydess.lilith.utility.ArrayKit;

import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Commands {
    private static final CopyOnWriteArrayList<AbstractCommand> COMMANDS = ArrayKit.asList(CopyOnWriteArrayList::new, new AbstractCommand[]{
            new PingCommand()
    });

    public Commands(final DiscordApi api) {
        api.addSlashCommandCreateListener(event -> {
            final SlashCommandInteraction interaction = event.getSlashCommandInteraction();
            Ashuramaru.getInstance().getLogger().info("hi");
            COMMANDS.stream()
                    .filter(command -> command.name().equalsIgnoreCase(interaction.getCommandName()))
                    .forEach(command -> command.interaction().accept(interaction));
        });
        COMMANDS.forEach(command -> SlashCommand.with(
                command.name().toLowerCase(Locale.ROOT),
                command.description(),
                command.options()
        ).createGlobal(api).join());
    }
}
