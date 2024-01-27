package rip.lunarydess.ashuramaru.config;

public class ConfigThrowables {
    public static final class File {
        public static final class NotCreatable extends Throwable {
        }

        public static final class NotWritable extends Throwable {
        }
    }

    public static final class WrongConfigVersion extends Throwable {
        private static final String ERROR = "is a invalid config version!";

        public WrongConfigVersion(final int version) {
            super(String.format("%d %s", version, ERROR));
        }
    }

}
