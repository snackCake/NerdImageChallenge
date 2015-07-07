package com.nerdery.imagechallenge.services.filters;

import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
@Component
public class ColorRotateFilter implements ImageFilter {

    @Override
    public String getName() {
        return "ColorRotate";
    }

    @Override
    public BufferedImage transform(BufferedImage sourceImage) {
        int sourceWidth = sourceImage.getWidth();
        int sourceHeight = sourceImage.getHeight();
        BufferedImage targetImage = new BufferedImage(sourceWidth, sourceHeight, sourceImage.getType());
        IntStream.range(0, sourceHeight).parallel().forEach(y -> IntStream.range(0, sourceWidth).forEach(x -> {
            int pixel = sourceImage.getRGB(x, y);
            int red = pixel & 0x00FF0000;
            int green = pixel & 0x0000FF00;
            int blue = pixel & 0x000000FF;
            int rotatedPixel = (red >> 8) | (green >> 8) | (blue << 16);
            targetImage.setRGB(x, y, rotatedPixel);
        }));
        return targetImage;
    }
}
