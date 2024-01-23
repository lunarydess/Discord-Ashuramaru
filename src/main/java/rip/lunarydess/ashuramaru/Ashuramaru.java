package rip.lunarydess.ashuramaru;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.user.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rip.lunarydess.ashuramaru.command.Commands;

public final class Ashuramaru {
    private static final Ashuramaru INSTANCE = new Ashuramaru();

    private final Logger logger = LoggerFactory.getLogger("Ashuramaru");

    public static void main(final String... args) {
        INSTANCE.run();
    }

    public static Ashuramaru getInstance() {
        return INSTANCE;
    }

    public void run() {
        String token = "MTE0NjA0MDU3NzY3MTA1NzUwOQ.GeQl58.lN5M2tJqmJ1Dy-hF-POmtYRjWwEuuYszQNu3V4";

        final DiscordApi api = new DiscordApiBuilder()
                .setToken(token)
                .addIntents(Intent.values())
                .setWaitForServersOnStartup(true)
                .setTrustAllCertificates(true)
                .login().join();

        api.updateStatus(UserStatus.DO_NOT_DISTURB);
        new Commands(api);
        logger.info(String.format("You can invite the bot by using the following url: %s", api.createBotInvite(Permissions.fromBitmask(Intent.calculateBitmask(Intent.values())))));
    }

    public Logger getLogger() {
        return this.logger;
    }
}
