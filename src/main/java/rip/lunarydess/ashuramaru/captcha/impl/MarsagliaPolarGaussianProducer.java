package rip.lunarydess.ashuramaru.captcha.impl;

import net.logicsquad.nanocaptcha.image.noise.NoiseProducer;
import rip.lunarydess.lilith.math.Arithmetics;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author lunarydess, logicsquad
 * @implNote {@link net.logicsquad.nanocaptcha.image.noise.GaussianNoiseProducer GaussianNoiseProducer}
 * @see <a href="https://en.wikipedia.org/wiki/Marsaglia_polar_method">Wikipedia</a>
 */
public final class MarsagliaPolarGaussianProducer implements NoiseProducer {
    private static final int DEFAULT_STANDARD_DEVIATION = 20;
    private static final int DEFAULT_MEAN = 0;

    private final int standardDeviation, mean;
    private boolean lineEnabled;

    // @formatter:off
    public MarsagliaPolarGaussianProducer()
    { this(DEFAULT_STANDARD_DEVIATION, DEFAULT_MEAN); }

    public MarsagliaPolarGaussianProducer(
            final int standardDeviation,
            final int mean
    ) { this(standardDeviation, mean, false); }
    // @formatter:on

    public MarsagliaPolarGaussianProducer(
            final int standardDeviation,
            final int mean,
            final boolean lineEnabled
    ) {
        this.standardDeviation = standardDeviation;
        this.mean = mean;
        this.lineEnabled = lineEnabled;
    }

    public @Override void makeNoise(final BufferedImage image) {
        final WritableRaster raster = image.getRaster();
        final int[] iData = new int[raster.getSampleModel().getNumBands()];
        for (int y = 0; y < raster.getHeight(); ++y) {
            for (int x = 0; x < raster.getWidth(); ++x) {
                final int[] pixelSamples = raster.getPixel(x, y, iData);
                for (int i = 0; i < pixelSamples.length; ++i) {
                    pixelSamples[i] = Arithmetics.clamp(
                            (int) (pixelSamples[i] + (ThreadLocalRandom.current().nextGaussian() * this.standardDeviation) + this.mean),
                            0, 255
                    );
                }
                raster.setPixel(x, y, pixelSamples);
            }
        }
    }
    
}
