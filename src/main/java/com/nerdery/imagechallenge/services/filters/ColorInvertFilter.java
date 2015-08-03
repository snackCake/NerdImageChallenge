package com.nerdery.imagechallenge.services.filters;

import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

/**
 * Super-easy and straight-forward filter that just inverts each pixel.<p/>
 *
 * The result looks pretty cool, but it isn't very creative...<p/>
 *
 * <em>Adapted from {@link ColorRotateFilter}.</em>
 *
 * @author Ryan Evans (revans@nerdery.com)
 */
@Component
public class ColorInvertFilter implements ImageFilter {

    @Override
    public String getName() {
        return "ColorInvert";
    }

    @Override
    public BufferedImage transform(BufferedImage sourceImage) {
        int sourceWidth = sourceImage.getWidth();
        int sourceHeight = sourceImage.getHeight();
        BufferedImage targetImage = new BufferedImage(sourceWidth, sourceHeight, sourceImage.getType());
        IntStream.range(0, sourceHeight).parallel().forEach(y -> IntStream.range(0, sourceWidth).forEach(x -> {
            int pixel = sourceImage.getRGB(x, y);
            // invert the pixel while preserving the alpha bits
            int invertedPixel = (0x00ffffff - (pixel | 0xff000000)) | (pixel & 0xff000000);
            targetImage.setRGB(x, y, invertedPixel);
        }));
        return targetImage;
    }
}
