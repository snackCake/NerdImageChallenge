package com.nerdery.imagechallenge.services.filters.revans;

import com.nerdery.imagechallenge.services.filters.ImageFilter;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

/**
 * An adaptation of {@link com.nerdery.imagechallenge.services.filters.OilPaintFilter} which performs better.
 *
 * @author Ryan Evans (revans)
 */
@Component
public class FasterOilPaintFilter implements ImageFilter {

    // filter constants - change values to alter effect
    private static final int RADIUS = 6;
    private static final int LEVELS = 9;

    // calculated constants
    private static final int RADIUS2 = RADIUS * RADIUS;
    private static final int DIAMETER = RADIUS * 2;
    private static final int DIAMETER2 = DIAMETER * DIAMETER;

    // band constants
    private static final int RGB = 3;
    private static final int ARGB = 4;

    private static boolean withinCircle(int x, int y) {
        return (x * x + y * y) < RADIUS2;
    }

    @Override
    public String getName() {
        return "revans-FasterOilPaintFilter";
    }

    @Override
    public BufferedImage transform(BufferedImage sourceImage) {
        return new Instance(sourceImage).transform();
    }

    private static class Instance {

        final BufferedImage sourceImage;
        BufferedImage targetImage;

        int width;
        int height;
        int bands = RGB;

        int[] sourcePixels;
        int[] targetPixels;

        final int[][] levelBuckets = new int[LEVELS][];
        final int [] levelBucketSizes = new int[LEVELS];
        int largestLevelBucket;

        Instance(BufferedImage sourceImage) {
            this.sourceImage = sourceImage;
            init();
        }

        private void init() {
            width = sourceImage.getWidth();
            height = sourceImage.getHeight();

            if (sourceImage.getColorModel().hasAlpha()) {
                bands = ARGB;
            }

            sourcePixels = sourceImage.getRaster().getPixels(0, 0, width, height, new int[width * height * bands]);
            targetPixels = new int[width * height * bands];

            for (int i = 0; i < LEVELS; i++) {
                // initialize each bucket to the max size that may be needed
                levelBuckets[i] = new int[DIAMETER2 * bands];
            }

            targetImage = new BufferedImage(width, height, sourceImage.getType());
        }

        BufferedImage transform() {
            for (int y = 0; y < height; y++) {
                transformRow(y);
            }
            targetImage.getRaster().setPixels(0, 0, width, height, targetPixels);
            return targetImage;
        }

        private void transformRow(int y) {
            for (int x = 0; x < width; x++) {
                transformPixel(x, y);
            }
        }

        private void transformPixel(int x, int y) {
            resetLevelBucketSizes();
            organizeNeighboringPixelsIntoLevelBuckets(x, y);
            averageLargestBucketAndSetPixel(x, y);
        }

        private void resetLevelBucketSizes() {
            largestLevelBucket = 0;
            for (int i = 0; i < LEVELS; i++) {
                levelBucketSizes[i] = 0;
            }
        }

        private void organizeNeighboringPixelsIntoLevelBuckets(int x, int y) {
            for (int dX = -RADIUS; dX < RADIUS; dX++) {
                int pX = x + dX;
                if (!withinBoundsX(pX)) continue;
                for (int dY = -RADIUS; dY < RADIUS; dY++) {
                    int pY = y + dY;
                    if (withinBoundsY(pY) && withinCircle(dX, dY)) {
                        int o = getPixelOffset(pX, pY);
                        int r = sourcePixels[o];
                        int g = sourcePixels[o + 1];
                        int b = sourcePixels[o + 2];
                        int level = calculateLevel(r, g, b);
                        int size = levelBucketSizes[level];
                        int[] bucket = levelBuckets[level];
                        bucket[size] = r;
                        bucket[size + 1] = g;
                        bucket[size + 2] = b;
                        size += bands;
                        levelBucketSizes[level] = size;
                        if (size > levelBucketSizes[largestLevelBucket]) {
                            largestLevelBucket = level;
                        }
                    }
                }
            }
        }

        private boolean withinBoundsX(int x) {
            return x >= 0 && x < width;
        }

        private boolean withinBoundsY(int y) {
            return y >= 0 && y < height;
        }

        private int calculateLevel(int r, int g, int b) {
            return (((r + g + b) / 3) * (LEVELS - 1)) / 255;
        }

        private void averageLargestBucketAndSetPixel(int x, int y) {
            int[] largestBucket = levelBuckets[largestLevelBucket];
            int largestBucketSize = levelBucketSizes[largestLevelBucket];
            int largestBucketCount = largestBucketSize / bands;

            int ar = 0;
            int ag = 0;
            int ab = 0;
            for (int i = 0; i < largestBucketSize; i += bands) {
                ar += largestBucket[i];
                ag += largestBucket[i + 1];
                ab += largestBucket[i + 2];
            }

            int o = getPixelOffset(x, y);
            targetPixels[o] = ar / largestBucketCount;
            targetPixels[o + 1] = ag / largestBucketCount;
            targetPixels[o + 2] = ab / largestBucketCount;
            if (bands == ARGB)
                targetPixels[o + 3] = sourcePixels[o + 3];
        }

        private int getPixelOffset(int x, int y) {
            return (y * width + x) * bands;
        }
    }
}
