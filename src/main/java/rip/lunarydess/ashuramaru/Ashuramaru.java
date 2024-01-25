package rip.lunarydess.ashuramaru;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.user.UserStatus;
import org.javacord.api.interaction.SlashCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
final ExecutorService activityExecServ = Executors.newSingleThreadExecutor();
final Runnable activityExec = () -> {
    ActivityType type;
    String activity;
    boolean next = false;
    while (true) {
        try {
            if (next) {
                type = ActivityType.LISTENING;
                activity = String.format("%d server", api.getServers().size());
            } else {
                type = ActivityType.WATCHING;
                activity = String.format(
                        "~ %d people",
                        api.getCachedUsers()
                                .stream()
                                .filter(user -> !user.isBot())
                                .count()
                );
            }

            Thread.sleep(5000L + ThreadLocalRandom.current().nextLong(5000L));
            next = !next;
        } catch (final Throwable throwable) {
            type = ActivityType.CUSTOM;
            activity = "";
            logger.error(throwable.getMessage(), throwable);
        }
        api.updateActivity(type, activity);
    }
};
api.addLostConnectionListener(event -> activityExecServ.shutdown());
api.addResumeListener(event -> activityExecServ.execute(activityExec));
activityExecServ.execute(activityExec);
 */
public final class Ashuramaru {
    private static final Ashuramaru INSTANCE = new Ashuramaru();

    private final Logger logger = LoggerFactory.getLogger(Ashuramaru.class);

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
                .addIntents(Intent.values()) // let's have an existential crisis :D
                .setWaitForServersOnStartup(true)
                .setTrustAllCertificates(false)
                .setUserCacheEnabled(true)
                .login().join();
        api.updateStatus(UserStatus.DO_NOT_DISTURB);

        SlashCommand.with(
                "ashuramaru",
                "Navigate through here for Ashuramaru's commands."
        ).createGlobal(api).join();

        logger.info(String.format(
                "You can invite the bot by using the following url: %s",
                api.createBotInvite(Permissions.fromBitmask(Intent.calculateBitmask(Intent.values())))
        ));
    }
}
