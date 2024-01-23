package rip.lunarydess.ashuramaru;

import java.io.File;

public final class Ashuramaru {
    private static final Ashuramaru INSTANCE = new Ashuramaru();
    private final File configFile = new File("config.toml");
    
    public static void main(final String... args) {
        INSTANCE.run();
    }
    
    public void run() {
    }
}
