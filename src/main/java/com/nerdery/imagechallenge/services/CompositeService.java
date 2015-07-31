package com.nerdery.imagechallenge.services;

import com.nerdery.imagechallenge.services.filters.FilterResult;
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
    private Color textColor;
    private String fontName;
    private int fontSize;

    public BufferedImage buildComposite(BufferedImage sourceImage, FilterResult targetImageResult) {
        int sourceHeight = sourceImage.getHeight();
        int sourceWidth = sourceImage.getWidth();
        BufferedImage finalImage = new BufferedImage(sourceWidth, sourceHeight * 2 + 1, sourceImage.getType());
        Graphics graphics = finalImage.getGraphics();
        graphics.drawImage(sourceImage, 0, 0, null);
        graphics.setColor(separatorColor);
        graphics.drawLine(0, sourceHeight, sourceWidth, sourceHeight);
        graphics.drawImage(targetImageResult.getOutputImage(), 0, sourceHeight + 1, null);
        char[] milliSecondsString = (targetImageResult.getResultTime().toString() + " ms").toCharArray();
        graphics.setColor(textColor);
        graphics.setFont(new Font(fontName, Font.PLAIN, fontSize));
        int margin = graphics.getFontMetrics().getHeight();
        graphics.drawChars(milliSecondsString, 0 , milliSecondsString.length, margin, 2 * margin);
        return finalImage;
    }

    @Inject
    public void setSeparatorColorHex(@Value("${imagechallenge.separatorcolor}") String hexColor) throws NumberFormatException {
        separatorColor = Color.decode(hexColor);
    }

    @Inject
    public void setTextColorHex(@Value("${imagechallenge.textcolor}") String hexColor) throws NumberFormatException {
        textColor = Color.decode(hexColor);
    }

    @Inject
    public void setFontName(@Value("${imagechallenge.fontname}")String fontName) {
        this.fontName = fontName;
    }

    @Inject
    public void setFontSize(@Value("${imagechallenge.fontsize}")int fontSize) {
        this.fontSize = fontSize;
    }
}
