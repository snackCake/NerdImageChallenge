package com.nerdery.imagechallenge.services.filters;

import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;

/**
 * This filter produces an image that resembles an "old time"-like photograph.<p/>
 *
 * First, the image is convolved. This has the effect of blurring and brightening the image. Next, a sepia effect
 * is applied to the image.<p/>
 *
 * @author Ryan Evans (revans@nerdery.com)
 */
@Component
public class OldTimeyEffectFilter implements ImageFilter {

    /* using a 3x3 matrix where the numbers add-up to MORE than 1 causes a brightening effect */
    private static final float[] CONVOLVE_MATRIX = {
            0.155f, 0.155f, 0.155f,
            0.155f, 0.155f, 0.155f,
            0.155f, 0.155f, 0.155f,
    };

    /* these numbers can be played around with */
    private static final int SEPIA_DEPTH = 20;
    private static final int SEPIA_INTENSITY = 25;

    @Override
    public String getName() {
        return "OldTimeyEffect";
    }

    @Override
    public BufferedImage transform(BufferedImage sourceImage) {
        BufferedImage targetImage = convolveImage(sourceImage);
        applySepiaToImage(targetImage);
        return targetImage;
    }

    private BufferedImage convolveImage(BufferedImage sourceImage) {
        BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, CONVOLVE_MATRIX));
        return op.filter(sourceImage, null);
    }

    private void applySepiaToImage(BufferedImage image) {
        int sourceWidth = image.getWidth();
        int sourceHeight = image.getHeight();

        WritableRaster raster = image.getRaster();

        int[] pixels = new int[sourceWidth * sourceHeight * 3];
        raster.getPixels(0, 0, sourceWidth, sourceHeight, pixels);

        for (int i = 0; i < pixels.length; i += 3) {
            int grayRGB = (pixels[i] + pixels[i + 1] + pixels[i + 2]) / 3;
            pixels[i] = Math.min(255, grayRGB + (SEPIA_DEPTH * 2));
            pixels[i + 1] = Math.min(255, grayRGB + SEPIA_DEPTH);
            pixels[i + 2]= Math.max(0, grayRGB - SEPIA_INTENSITY);
        }

        raster.setPixels(0, 0, sourceWidth, sourceHeight, pixels);
    }
}
