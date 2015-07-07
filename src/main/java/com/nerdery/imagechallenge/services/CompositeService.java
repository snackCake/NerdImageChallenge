package com.nerdery.imagechallenge.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
@Service
public class CompositeService {

    private Color separatorColor;

    public BufferedImage buildComposite(BufferedImage sourceImage, BufferedImage targetImage) {
        int sourceHeight = sourceImage.getHeight();
        int sourceWidth = sourceImage.getWidth();
        BufferedImage finalImage = new BufferedImage(sourceWidth, sourceHeight * 2 + 1, sourceImage.getType());
        Graphics graphics = finalImage.getGraphics();
        graphics.drawImage(sourceImage, 0, 0, null);
        graphics.setColor(separatorColor);
        graphics.drawLine(0, sourceHeight, sourceWidth, sourceHeight);
        graphics.drawImage(targetImage, 0, sourceHeight + 1, null);
        return finalImage;
    }

    @Inject
    public void setSeparatorColorHex(@Value("${imagechallenge.separatorcolor}") String hexColor) throws NumberFormatException {
        separatorColor = Color.decode(hexColor);
    }
}
