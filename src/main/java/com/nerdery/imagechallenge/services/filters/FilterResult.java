package com.nerdery.imagechallenge.services.filters;

import java.awt.image.BufferedImage;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class FilterResult {
    private Long resultTime;
    private BufferedImage outputImage;

    public FilterResult(Long resultTime, BufferedImage outputImage) {
        this.resultTime = resultTime;
        this.outputImage = outputImage;
    }

    public Long getResultTime() {
        return resultTime;
    }

    public BufferedImage getOutputImage() {
        return outputImage;
    }
}
