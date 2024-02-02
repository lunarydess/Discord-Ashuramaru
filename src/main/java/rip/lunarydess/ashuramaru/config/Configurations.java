package rip.lunarydess.ashuramaru.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.ErrorReportConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.toml.TomlFactory;
import com.fasterxml.jackson.dataformat.toml.TomlReadFeature;
import com.fasterxml.jackson.dataformat.toml.TomlWriteFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rip.lunarydess.ashuramaru.Ashuramaru;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;

public final class Configurations {
    private static final int CONFIG_VERSION = 1;

    private static final Path CONFIG_PATH = Path.of("config.toml");
    private static final String DEFAULT_CONFIG = """
            config_version = 1
            \n
            [general]
            bot_token = ""
            enabled_intents = [ # let's have an existential crisis :D
                 0, # guilds
                 1, # privileged - guild members
                 2, # guild bans
                 3, # guild emojis
                 4, # guild integrations
                 5, # guild webhooks
                 6, # guild invites
                 7, # guild voice states
                 8, # privileged - guild presences
                 9, # guild messages
                10, # guild message reactions
                11, # guild message typing
                12, # direct messages
                13, # direct message reactions
                14, # direct message typing
                15, # privileged - message contents
                16  # guild scheduled events
            ]
            """;

    private final @NotNull Consumer<Throwable> onError;
    private final ObjectMapper mapper;

    private @Nullable ConfigValues data;

    public Configurations() {
        this(Throwable::printStackTrace);
    }

    public Configurations(final @NotNull Consumer<Throwable> onError) {
        this.onError = onError;
        this.mapper = new ObjectMapper(TomlFactory.builder()
                .errorReportConfiguration(ErrorReportConfiguration.defaults())
                .enable(TomlReadFeature.PARSE_JAVA_TIME)
                .enable(TomlWriteFeature.FAIL_ON_NULL_WRITE)
                .build());
        try {
            if (Files.exists(CONFIG_PATH)) break creationBlock;
            if (Files.notExists(Files.createFile(CONFIG_PATH))) {
                onError.accept(new ConfigThrowables.File.NotCreatable());
                return;
            }
            Files.writeString(
                    CONFIG_PATH, DEFAULT_CONFIG,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND
            );
        } catch (final Throwable throwable) {
            onError.accept(throwable);
        }
    }

    public boolean load() {
        try {
            Ashuramaru.getInstance()
                    .getLogger()
                    .debug((this.data = mapper.readValue(CONFIG_PATH.toFile(), ConfigValues.class)).toString());
            if (this.data().configVersion() == CONFIG_VERSION) {
                return true;
            }
            this.onError.accept(new ConfigThrowables.WrongConfigVersion(this.data.configVersion()));
        } catch (final Throwable throwable) {
            this.data = null;
            this.onError.accept(throwable);
        }
        return false;
    }

    public boolean save() {
        /* TODO: ... | not implemented yet
        boolean saved = false;
        try {
            this.mapper
                    .writerFor(ConfigValues.class)
                    .writeValues(CONFIG_PATH.toFile())
                    .close();
            saved = true;
        } catch (final Throwable throwable) {
            this.onError.accept(throwable);
        }
        return saved;
         */
        return true;
    }

    public ConfigValues data() {
        return this.data;
    }

    public record ConfigValues(
            @JsonProperty("config_version") int configVersion,
            @JsonProperty("general") Configurations.ConfigValues.General general
    ) {
        public record General(
                @JsonProperty("bot_token") String botToken,
                @JsonProperty("enabled_intents") int[] enabledIntents
        ) {
            @Override
            public String toString() {
                return new StringBuilder("General{")
                        .append("botToken='").append(botToken).append('\'')
                        .append('}')
                        .toString();
            }
        }
    }
}
