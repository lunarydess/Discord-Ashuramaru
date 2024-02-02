package rip.lunarydess.ashuramaru;

import net.logicsquad.nanocaptcha.image.ImageCaptcha;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.ServerVoiceChannelBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.user.UserStatus;
import org.javacord.api.interaction.SlashCommand;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rip.lunarydess.ashuramaru.captcha.Captcha;
import rip.lunarydess.ashuramaru.config.Configurations;
import rip.lunarydess.lilith.utility.ArrayKit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

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
    private DiscordApi discordApi = null;

    public static void main(final String... args) {
        final ImageCaptcha imageCaptcha = Captcha.generate().join();
        final JFrame frame = new JFrame();
        frame.setTitle("captcha test");

        final ImageIcon icon = new ImageIcon(imageCaptcha.getImage());
        final JLabel imageLabel = new JLabel(icon);
        frame.getContentPane().add(imageLabel, BorderLayout.CENTER);
        frame.setVisible(true);

        final JButton generateButton = new JButton("generate");
        generateButton.addMouseListener(new MouseAdapter() {
            public @Override void mouseClicked(final MouseEvent event) {
                final ImageCaptcha captcha = Captcha.generate().join();
                icon.setImage(captcha.getImage());
                imageLabel.updateUI();
                System.out.println(captcha.getContent());
            }
        });
        frame.getContentPane().add(generateButton, BorderLayout.SOUTH);

        System.out.println(imageCaptcha.getContent());
        INSTANCE.run();
    }

    public static Ashuramaru getInstance() {
        return INSTANCE;
    }

    public void run() {
        this.getLogger().info("""
                \n
                          __             __                  __      \s
                     /\\  /__` |__| |  | |__)  /\\  |\\/|  /\\  |__) |  |\s
                    /~~\\ .__/ |  | \\__/ |  \\ /~~\\ |  | /~~\\ |  \\ \\__/\s
                \n
                \t~ project by lunarydess and contribs ~
                \t             made with \u2661
                \t        powered with javacord
                """);
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
                ).replaceAll(",", ",\n\t")
                .replaceAll("\\u005b", "\u005b\n\t ")
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
            final String headerFooter = "=".repeat(42);
            // @formatter:off
            this.getLogger().error("""
                    {}
                    Couldn't connect with given token.
                    Check your internet connection + token.
                    {}""", headerFooter, headerFooter);
            // @formatter:on
            this.close();
            return;
        }

        final BiConsumer<ServerVoiceChannel, User> move = (channel, user) -> {
            if (channel.getId() != 1200897374906753094L) return;
            final ServerVoiceChannel newChannel = new ServerVoiceChannelBuilder(channel.getServer())
                    .setName(String.format("%s", user.getName()))
                    .setUserlimit(1)
                    .setBitrate(64000)
                    .setCategory(channel.getCategory().get())
                    .create().join();
            user.move(newChannel);
            newChannel.addServerVoiceChannelMemberLeaveListener(leaveEvent -> {
                if (leaveEvent.getUser().getId() != user.getId()) return;
                newChannel.delete();
            });
        };

        this.discordApi.addServerVoiceChannelMemberJoinListener(event -> move.accept(event.getChannel(), event.getUser()));
        CompletableFuture.runAsync(() -> {
            for (final Server server : this.discordApi.getServers()) {
                for (final ServerVoiceChannel channel : server.getVoiceChannels()) {
                    if (channel.getId() != 1200897374906753094L) continue;
                    for (final User user : channel.getConnectedUsers())
                        move.accept(channel, user);
                    break;
                }
            }
        });

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
        this.getLogger().info("saving...");

        this.getLogger().info("\t> saving config...");
        this.getLogger().info(String.format("\t> ...%s", this.getConfig().save() ? "saved config!" : "couldn't save config!"));

        this.getLogger().info("...done, cya!");

        System.exit(0);
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
