package com.nerdery.imagechallenge.sanic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class SanicUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(SanicUtils.class);
    public static void logDuration(final String name, final long duration) {
        LOGGER.info("Section \'{}\' took \'{}\' milliseconds.", name, TimeUnit.MILLISECONDS.convert(duration, TimeUnit.NANOSECONDS));
    }
}
