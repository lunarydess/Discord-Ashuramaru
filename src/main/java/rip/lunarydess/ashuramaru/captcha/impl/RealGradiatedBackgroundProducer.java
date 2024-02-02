package rip.lunarydess.ashuramaru.captcha.impl;

import net.logicsquad.nanocaptcha.image.backgrounds.BackgroundProducer;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * @author lunarydess, logicsquad
 * @implNote {@link net.logicsquad.nanocaptcha.image.backgrounds.GradiatedBackgroundProducer}
 */
public final class RealGradiatedBackgroundProducer implements BackgroundProducer {
    private static final Color[] DEFAULT_COLORS = { // let's be gay :D
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.GREEN,
            Color.BLUE,
            Color.MAGENTA.darker(),
            Color.MAGENTA
    };

    private final Color[] currentColors;

    public RealGradiatedBackgroundProducer() {
        this(DEFAULT_COLORS);
    }

    public RealGradiatedBackgroundProducer(final Color... colors) {
        this.currentColors = colors;
    }

    public BufferedImage getBackground(
            final int width,
            final int height
    ) {
        final BufferedImage image = new BufferedImage(width, height, ColorSpace.TYPE_Lab);
        final Graphics2D graphics = image.createGraphics();

        graphics.setRenderingHints(Map.of(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON,

                RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY,

                RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY
        ));

        final double stepSize = 1.0D / (currentColors.length - 1.0D);
        for (int i = 1; i < currentColors.length; i++) {
            final float
                    startY = (float) ((i - 1) * stepSize * height),
                    endY = (float) (i * stepSize * height);
            graphics.setPaint(new GradientPaint(0, startY, currentColors[i - 1], 0, endY, currentColors[i]));
            graphics.fill(new Rectangle2D.Double(0.0, startY, width, endY - startY));
        }

        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        return image;
    }
}
