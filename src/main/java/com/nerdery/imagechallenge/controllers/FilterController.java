package com.nerdery.imagechallenge.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.stream.IntStream;

@Controller
public class FilterController {

    @RequestMapping("/filter")
    public ResponseEntity<byte[]> testphoto() throws IOException, URISyntaxException {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        BufferedImage sourceImage = ImageIO.read(new URL("http://media.bizj.us/view/img/923121/nerderynoodlerkensykora*750.jpg"));
        BufferedImage targetImage = transformImage(sourceImage);
        BufferedImage finalImage = buildFinalComposite(sourceImage, targetImage);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(finalImage, "JPEG", outputStream);
        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.CREATED);
    }

    private BufferedImage transformImage(BufferedImage sourceImage) {
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

    private BufferedImage buildFinalComposite(BufferedImage sourceImage, BufferedImage targetImage) {
        int sourceHeight = sourceImage.getHeight();
        int sourceWidth = sourceImage.getWidth();
        BufferedImage finalImage = new BufferedImage(sourceWidth, sourceHeight * 2 + 1, sourceImage.getType());
        Graphics graphics = finalImage.getGraphics();
        graphics.drawImage(sourceImage, 0, 0, null);
        graphics.setColor(Color.RED);
        graphics.drawLine(0, sourceHeight, sourceWidth, sourceHeight);
        graphics.drawImage(targetImage, 0, sourceHeight + 1, null);
        return finalImage;
    }
}
