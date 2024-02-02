package rip.lunarydess.ashuramaru.captcha.impl;

import net.logicsquad.nanocaptcha.content.AbstractContentProducer;
import rip.lunarydess.lilith.utility.StringKit;

public class AlphanumericContentProducer extends AbstractContentProducer {
    // @formatter:off
    private static final char[] DEFAULT_CHARS = (
            StringKit.getLowerChars() + 
            StringKit.getUpperChars() + 
            StringKit.getNumberChars()
    ).toCharArray();
    // @formatter:on

    public AlphanumericContentProducer() {
        this(7);
    }

    public AlphanumericContentProducer(final int length) {
        super(length, DEFAULT_CHARS.clone());
    }
}
