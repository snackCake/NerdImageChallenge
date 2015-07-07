package com.nerdery.imagechallenge.services.filters;

import java.awt.image.BufferedImage;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public interface ImageFilter {

    String getName();

    BufferedImage transform(BufferedImage sourceImage);
}
