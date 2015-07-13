package com.nerdery.imagechallenge.genetic;

import java.security.SecureRandom;

public class Colors {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int MAXIMUM_JITTER = 8;

    /**
     * Given a pixel, randomly jitter its colors. This method does not affect the alpha component of the pixel.
     *
     * @param pixel The pixel to jitter.
     * @return The randomized pixel value.
     */
    public static int jitterPixelColors(final int pixel) {
        final int a = (pixel >> 24) & 0xFF;
        final int r = (pixel >> 16) & 0xFF;
        final int g = (pixel >> 8)  & 0xFF;
        final int b = (pixel)       & 0xFF;

        return packColor(a, randomColorAdjustment(r), randomColorAdjustment(g), randomColorAdjustment(b));
    }

    /**
     * Given some base color value, adjust it randomly within a fixed tolerance.
     *
     * @param colorValue The base color value.
     * @return The randomly-adjusted color value.
     */
    public static int randomColorAdjustment(final int colorValue) {
        return boundColor(colorValue + (RANDOM.nextInt(MAXIMUM_JITTER) * ((RANDOM.nextBoolean()) ? -1 : 1)));
    }

    /**
     * Given component values of a pixel, pack those values back into a single pixel.
     * @param a The alpha component.
     * @param r The red component.
     * @param g The green component.
     * @param b The blue component.
     * @return The packed pixel value.
     */
    public static int packColor(final int a, final int r, final int g, final int b) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    /**
     * Given some color value, ensure that the value is within the valid range (0-255).
     *
     * @param colorValue The color value to bound.
     * @return The bounded color value.
     */
    public static int boundColor(final int colorValue) {
        if (colorValue > 255) return 255;
        if (colorValue < 0) return 0;
        return colorValue;
    }
}
