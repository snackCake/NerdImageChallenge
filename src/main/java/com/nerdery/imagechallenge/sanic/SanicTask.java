package com.nerdery.imagechallenge.sanic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.Raster;
import java.util.concurrent.Callable;

public class SanicTask implements Callable<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SanicTask.class);
    private final SanicChunk chunk;
    private final SanicShared config;

    public SanicTask(final SanicChunk chunk, final SanicShared config) {
        this.chunk = chunk;
        this.config = config;
    }

    @Override
    public Void call() {
        try {
            final int[] sourceColor = new int[3];
            final IntensityBucket[] buckets = new IntensityBucket[config.getLevels() + 1];
            for (int i = 0; i < buckets.length; i++) {
                buckets[i] = new IntensityBucket();
            }

            LOGGER.info("Task received chunk of work. Utilizing buffer indices {} through {}",
                    chunk.getBeginBufferInclusive(), chunk.getEndBufferInclusive());

            final int sourceWidth = chunk.getSourceWidth();
            final int sourceHeight = chunk.getSourceHeight();
            final Raster raster = chunk.getRaster();

            int bufferIndex = chunk.getBeginBufferInclusive();
            final long beginTask = System.nanoTime();
            for (int y = chunk.getBeginRowInclusive(); y <= chunk.getEndRowInclusive(); y++) {
                // We will usually overshoot the last chunk, so make sure we don't explode.
                if (y >= sourceHeight) {
                    break;
                }

                for (int x = 0; x < chunk.getSourceWidth(); x++) {
                    zeroBuckets(buckets);

                    final int X = x;
                    final int Y = y;
                    config.getRadialOffsets().forEach(v -> {
                        final int px = X + v.x;
                        final int py = Y + v.y;

                        // If the selected offset is in bounds, merge the pixel into the appropriate bucket.
                        if (isInBounds(px, py, sourceWidth, sourceHeight)) {
                            raster.getPixel(px, py, sourceColor);
                            // Yes, we recalculate this a lot, but it turns out that caching the results does not help
                            // increase the speed (I tried two different ways).
                            buckets[calculateIntensity(sourceColor)].merge(sourceColor);
                        }
                    });

                    final IntensityBucket bucket = biggestBucket(buckets);
                    config.getBuffer()[bufferIndex] = bucket.averageRed();
                    config.getBuffer()[bufferIndex + 1] = bucket.averageGreen();
                    config.getBuffer()[bufferIndex + 2] = bucket.averageBlue();
                    bufferIndex += 3;
                }
            }

            SanicUtils.logDuration("Individual Task", System.nanoTime() - beginTask);
        }
        catch (final Exception e) {
            LOGGER.error("ERROR", e);
            throw e;
        }

        return null;
    }

    private IntensityBucket biggestBucket(final IntensityBucket[] buckets) {
        IntensityBucket biggest = null;
        for (final IntensityBucket bucket : buckets) {
            if (biggest == null || bucket.size() > biggest.size()) {
                biggest = bucket;
            }
        }

        return biggest;
    }

    private boolean isInBounds(final int x, final int y, final int sourceWidth, final int sourceHeight) {
        return x >= 0 && x < sourceWidth && y >= 0 && y < sourceHeight;
    }

    private int calculateIntensity(int[] color) {
        return (((color[0] + color[1] + color[2]) / 3) * config.getLevels()) / 255;
    }

    private void zeroBuckets(final IntensityBucket[] buckets) {
        for (IntensityBucket bucket : buckets) {
            bucket.clear();
        }
    }
}
