package rip.lunarydess.ashuramaru;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.user.UserStatus;
import org.javacord.api.interaction.SlashCommand;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rip.lunarydess.ashuramaru.config.Configurations;
import rip.lunarydess.lilith.utility.ArrayKit;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

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
    private final Configurations config = new Configurations(
            throwable -> this.logger.error(throwable.getMessage(), throwable)
    );
    private final Thread onShutdown = new Thread(this::close);
    private DiscordApi discordApi = null;

    public static void main(final String... args) {
        INSTANCE.run();
    }

    public static Ashuramaru getInstance() {
        return INSTANCE;
    }

    public void run() {
        Runtime.getRuntime().addShutdownHook(this.onShutdown);
        this.getConfig().load();

        if (this.getConfig().data() == null) {
            this.getLogger().error("The config data is corrupted, recheck! Shutting down...");
            this.close();
            return;
        }

        final Intent[] enabledIntents = CompletableFuture.supplyAsync(() -> Arrays.stream(Intent.values())
                .filter(intent -> Arrays
                        .stream(this.getConfig().data().general().enabledIntents())
                        .anyMatch(value -> value == intent.getId()))
                .toArray(Intent[]::new)).join();

        logger.info(String.format("Found enabled intents: %s", ArrayKit.toString(intent -> intent
                                .toString()
                                .toLowerCase(Locale.ROOT)
                                .replaceAll("_", " "),
                        enabledIntents
                ).replaceAll(",", ",\n")
                .replaceAll("\\u005b", "\u005b\n ")
                .replaceAll("\\u005d", "\n\u005d")));

        try {
            (this.discordApi = new DiscordApiBuilder()
                    .setToken(this.getConfig().data().general().botToken())
                    .addIntents(enabledIntents)
                    .setWaitForServersOnStartup(true)
                    .setTrustAllCertificates(false)
                    .setUserCacheEnabled(true)
                    .login().join())
                    .updateStatus(UserStatus.DO_NOT_DISTURB);
        } catch (final Throwable throwable) {
            this.getLogger().error( // @formatter:off
                    "Couldn't connect with given token. " +
                    "Check your internet connection + token. " +
                    "If you believe this is an internal error, contact the developers behind this project!"
            ); // @formatter:on
            this.close();
            return;
        }

        SlashCommand.with(
                "ashuramaru",
                "Navigate through here for Ashuramaru's commands."
        ).createGlobal(this.getDiscordApi());

        logger.info(String.format(
                "You can invite the bot by using the following url: %s",
                this.getDiscordApi().createBotInvite(Permissions.fromBitmask(Intent.calculateBitmask(enabledIntents)))
        ));
    }

    public void close() {
        // TODO: ... | better workaround due to non-blocking closing as it does not allow me to close the application properly. D:
        this.getLogger().info("exit call received...");

        this.getLogger().info("\t> saving config...");
        this.getLogger().info(String.format("\t> %s", this.getConfig().save() ? "...saved config!" : "...couldn't save config!"));

        this.getLogger().info("...prepared for exiting successfully!");
    }

    public Logger getLogger() {
        return this.logger;
    }

    public Configurations getConfig() {
        return this.config;
    }

    public @NotNull DiscordApi getDiscordApi() {
        return this.discordApi;
    }

    public boolean isDebug() {
        return true;
    }
}
