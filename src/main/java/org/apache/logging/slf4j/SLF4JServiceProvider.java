package org.apache.logging.slf4j;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.tinylog.slf4j.ModernTinylogLoggerFactory;
import org.tinylog.slf4j.TinylogMdcAdapter;

public class SLF4JServiceProvider implements org.slf4j.spi.SLF4JServiceProvider {
    public static final String REQUESTED_API_VERSION = "2.0";

    private ILoggerFactory loggerFactory;
    private IMarkerFactory markerFactory;
    private MDCAdapter mdcAdapter;

    public SLF4JServiceProvider() {
    }

    public ILoggerFactory getLoggerFactory() {
        return this.loggerFactory;
    }

    public IMarkerFactory getMarkerFactory() {
        return this.markerFactory;
    }

    public MDCAdapter getMDCAdapter() {
        return this.mdcAdapter;
    }

    public String getRequestedApiVersion() {
        return REQUESTED_API_VERSION;
    }

    public void initialize() {
        this.loggerFactory = new ModernTinylogLoggerFactory();
        this.markerFactory = new BasicMarkerFactory();
        this.mdcAdapter = new TinylogMdcAdapter();
    }
}
