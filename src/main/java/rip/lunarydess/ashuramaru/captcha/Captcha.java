package rip.lunarydess.ashuramaru.captcha;

import net.logicsquad.nanocaptcha.image.ImageCaptcha;
import net.logicsquad.nanocaptcha.image.renderer.DefaultWordRenderer;
import rip.lunarydess.ashuramaru.Ashuramaru;
import rip.lunarydess.ashuramaru.captcha.impl.AlphanumericContentProducer;
import rip.lunarydess.ashuramaru.captcha.impl.MarsagliaPolarGaussianProducer;
import rip.lunarydess.ashuramaru.captcha.impl.RealGradiatedBackgroundProducer;
import rip.lunarydess.lilith.utility.ArrayKit;

import java.awt.*;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public final class Captcha {
    private static final Font FONT;

    static {
        try {
            FONT = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(Ashuramaru.class.getResource("/typewriter.ttf").getFile())).deriveFont(Font.PLAIN, 32.0F);
        } catch (final Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    private static final Color[] COLORS = {
            /*
            new Color(243, 139, 168, 255),
            new Color(235, 160, 172, 255),
            new Color(250, 179, 135, 255),
            new Color(249, 226, 175, 255),
            new Color(166, 227, 161, 255),
            new Color(148, 226, 213, 255),
            new Color(137, 220, 235, 255),
            new Color(116, 199, 236, 255),
            new Color(137, 180, 250, 255),
            new Color(180, 190, 254, 255)
            */
            new Color(91, 206, 250, 255),
            new Color(245, 169, 184, 255),
            new Color(255, 255, 255, 255),
            new Color(245, 169, 184, 255),
            new Color(91, 206, 250, 255)
    };

    public static CompletableFuture<ImageCaptcha> generate() {
        return CompletableFuture.supplyAsync(() -> new ImageCaptcha.Builder(640, 360)
                .addContent(new AlphanumericContentProducer(8), new DefaultWordRenderer.Builder()
                        .xOffset(0.3 * ThreadLocalRandom.current().nextDouble())
                        .yOffset(0.8 * ThreadLocalRandom.current().nextDouble())
                        .randomColor(COLORS[0].darker(), ArrayKit.sliceFrom(
                                Color[]::new, Arrays.stream(COLORS).map(Color::darker).toArray(Color[]::new), 1
                        )).font(FONT).build())
                .addBackground(new RealGradiatedBackgroundProducer(COLORS)).addBorder()
                .addNoise(new MarsagliaPolarGaussianProducer(20, 4, false))
                .build());
    }
}
