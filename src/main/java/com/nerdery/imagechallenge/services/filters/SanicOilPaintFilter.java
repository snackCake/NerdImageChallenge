package com.nerdery.imagechallenge.services.filters;

import com.google.common.util.concurrent.Futures;
import com.nerdery.imagechallenge.sanic.SanicChunk;
import com.nerdery.imagechallenge.sanic.SanicShared;
import com.nerdery.imagechallenge.sanic.SanicTask;
import com.nerdery.imagechallenge.sanic.SanicUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.util.concurrent.*;

/**
 * This is a re-implementation of the OilPaintFilter, but it goes super fast like Sanic Hegehog.
 */
@Component
public class SanicOilPaintFilter implements ImageFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SanicOilPaintFilter.class);
    public static final String NAME = "GottaGoFast";

    @Autowired
    Environment environment;

    private SanicShared config;
    private ExecutorService executorService;
    private CompletionService<Void> completionService;

    public SanicOilPaintFilter() {}

    @Override
    public String getName() {
        return NAME;
    }

    @PostConstruct
    private void configureFilter() {
        config = new SanicShared(environment);
        executorService = Executors.newFixedThreadPool(config.getPoolSize());
        completionService = new ExecutorCompletionService<>(executorService);
        LOGGER.info("Initializing Sanic thread pool with {} members", config.getPoolSize());
    }

    @Override
    public BufferedImage transform(final BufferedImage sourceImage) {
        if (sourceImage.getColorModel().getPixelSize() > (8 * config.getBufferChannels())) {
            LOGGER.warn("Cannot process an image that does not match the configured color model.");
            return sourceImage;
        }

        final int width = sourceImage.getWidth();
        final int height = sourceImage.getHeight();
        final int rasterSize = width * height * config.getBufferChannels();
        LOGGER.info("Received a request to process an image with width {}, height {}, and raster size {}", width, height, rasterSize);

        if (rasterSize > config.getBufferSize()) {
            LOGGER.warn("Cannot process an image that exceeds the buffer size.");
            return sourceImage;
        }

        LOGGER.info("Segmenting image into processing chunks - each block of rows will contain {} pixels.", config.getRowCount() * width);
        long beginTime = System.nanoTime();
        int taskCount = 0;
        for (int i = 0; i < height; i += config.getRowCount()) {
            final SanicChunk chunk = new SanicChunk();
            chunk.setBeginRowInclusive(i);
            chunk.setEndRowInclusive(i + config.getRowCount() - 1);
            LOGGER.debug("Creating chunk spanning rows {} to {}", chunk.getBeginRowInclusive(), chunk.getEndRowInclusive());
            chunk.setBeginBufferInclusive(i * width * 3);
            chunk.setEndBufferInclusive((i * width * 3) + (width * 3 * config.getRowCount()) - 1);
            LOGGER.debug("Chunk utilizing buffer indices {} to {}", chunk.getBeginBufferInclusive(), chunk.getEndBufferInclusive());
            chunk.setRaster(sourceImage.getRaster());
            chunk.setSourceWidth(width);
            chunk.setSourceHeight(height);

            completionService.submit(new SanicTask(chunk, config));
            taskCount++;
        }

        int completed = 0;
        try {
            while (completed < taskCount) {
                completionService.take();
                completed++;
            }

            SanicUtils.logDuration("Sanic Processing Time", System.nanoTime() - beginTime);
        }
        catch (final InterruptedException e) {
            LOGGER.error("Interrupted during computation.");
            return sourceImage;
        }

        final BufferedImage result = new BufferedImage(width, height, sourceImage.getType());
        result.getRaster().setPixels(0, 0, width, height, config.getBuffer());

        return result;
    }
}
