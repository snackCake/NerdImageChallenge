package com.nerdery.imagechallenge.services.filters.revans;

import com.nerdery.imagechallenge.services.filters.ImageFilter;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

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

    // band constants
    private static final int RGB = 3;
    private static final int ARGB = 4;

    /**
     * Use the following formula to determine if x & y are within RADIUS:
     *
     * x<sup>2</sup> + y<sup>2</sup> &lt; <code>RADIUS</code><sup>2</sup>
     */
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

    /**
     * Wrap logic and state in inner class so as to allow this to be used as a stateless bean.
     */
    private static class Instance {

        final BufferedImage targetImage;
        final int width;
        final int height;
        final int bands;
        final int[] sourcePixels;
        final int[] targetPixels;

        Instance(BufferedImage sourceImage) {
            width = sourceImage.getWidth();
            height = sourceImage.getHeight();

            if (sourceImage.getColorModel().hasAlpha()) {
                bands = ARGB;
            } else {
                bands = RGB;
            }

            sourcePixels = sourceImage.getRaster().getPixels(0, 0, width, height, new int[width * height * bands]);
            targetPixels = new int[width * height * bands];

            targetImage = new BufferedImage(width, height, sourceImage.getType());
        }

        BufferedImage transform() {
            // use a parallel IntStream to process each row - MOAR CORES == MOAR BETTER!!!
            IntStream.range(0, height).parallel().forEach(this::transformRow);
            targetImage.getRaster().setPixels(0, 0, width, height, targetPixels);
            return targetImage;
        }

        private void transformRow(int y) {
            // use another parallel InStream here, although it probably doesn't matter (won't improve performance)
            IntStream.range(0, width).parallel().forEach(x -> transformPixel(x, y));
        }

        private void transformPixel(int x, int y) {
            int largestBucket = 0;
            int[] bucketSizes = new int[LEVELS];
            int[] bucketR = new int[LEVELS];
            int[] bucketG = new int[LEVELS];
            int[] bucketB = new int[LEVELS];

            for (int dX = -RADIUS; dX < RADIUS; dX++) {
                int pX = x + dX;
                if (!withinBoundsX(pX)) continue;
                for (int dY = -RADIUS; dY < RADIUS; dY++) {
                    int pY = y + dY;
                    if (withinBoundsY(pY) && withinCircle(dX, dY)) {
                        int offset = getPixelOffset(pX, pY);
                        int r = sourcePixels[offset];
                        int g = sourcePixels[offset + 1];
                        int b = sourcePixels[offset + 2];
                        int level = calculateLevel(r, g, b);

                        bucketR[level] += r;
                        bucketG[level] += g;
                        bucketB[level] += b;

                        bucketSizes[level]++;
                        if (level != largestBucket && bucketSizes[level] > bucketSizes[largestBucket]) {
                            largestBucket = level;
                        }
                    }
                }
            }

            int bucketSize = bucketSizes[largestBucket];
            int offset = getPixelOffset(x, y);
            targetPixels[offset] = bucketR[largestBucket] / bucketSize;
            targetPixels[offset + 1] = bucketG[largestBucket] / bucketSize;
            targetPixels[offset + 2] = bucketB[largestBucket] / bucketSize;
            if (bands == ARGB) {
                targetPixels[offset + 3] = sourcePixels[offset + 3];
            }
        }

        private boolean withinBoundsX(int x) {
            return x >= 0 && x < width;
        }
        private boolean withinBoundsY(int y) {
            return y >= 0 && y < height;
        }

        /**
         * Calculate the level of intensity given the RGB color value.
         */
        private int calculateLevel(int r, int g, int b) {
            return (((r + g + b) / 3) * (LEVELS - 1)) / 255;
        }

        /**
         * Calculate the offset of the pixel at x, y given the width and number of color bands.
         */
        private int getPixelOffset(int x, int y) {
            return (y * width + x) * bands;
        }
    }
}
