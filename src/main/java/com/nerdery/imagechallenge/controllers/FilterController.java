package com.nerdery.imagechallenge.controllers;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.nerdery.imagechallenge.services.CompositeService;
import com.nerdery.imagechallenge.services.FilterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import static org.springframework.http.MediaType.*;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
@Controller
public class FilterController {

    private static final String DEFAULT_FILE_FORMAT = IMAGE_JPEG.getSubtype();
    private static final Map<String, MediaType> SUPPORTED_FILE_FORMATS = new ImmutableMap.Builder<String, MediaType>()
            .put(IMAGE_JPEG.getSubtype(), IMAGE_JPEG)
            .put(IMAGE_GIF.getSubtype(), IMAGE_GIF)
            .put(IMAGE_PNG.getSubtype(), IMAGE_PNG)
            .build();

    private CompositeService compositeService;
    private FilterService filterService;
    private String sourceUrl;

    @RequestMapping("/filter/{name}")
    public ResponseEntity<byte[]> getTransformedImage(@PathVariable("name") String filterName) throws IOException, URISyntaxException {
        return buildResponseEntity(filterName, DEFAULT_FILE_FORMAT);
    }

    @RequestMapping("/filter/{name}/{format}")
    public ResponseEntity<byte[]> getTransformedImage(@PathVariable("name") String filterName, @PathVariable("format") String fileFormat)
            throws IOException, URISyntaxException {
        String normalizedFormat = fileFormat.toLowerCase();
        if (!SUPPORTED_FILE_FORMATS.containsKey(normalizedFormat)) {
            String message = "Invalid file format. Valid Formats: [" + Joiner.on(", ").join(SUPPORTED_FILE_FORMATS.keySet()) + "]";
            return new ResponseEntity<>(message.getBytes(), HttpStatus.BAD_REQUEST);
        }
        return buildResponseEntity(filterName, normalizedFormat);
    }

    private ResponseEntity<byte[]> buildResponseEntity(@PathVariable("name") String filterName, String fileFormat) throws IOException {
        BufferedImage sourceImage = ImageIO.read(new URL(sourceUrl));
        Optional<BufferedImage> targetImage = filterService.transformImage(sourceImage, filterName);
        if (targetImage.isPresent()) {
            return buildSuccessResponse(compositeService.buildComposite(sourceImage, targetImage.get()), fileFormat);
        } else {
            return new ResponseEntity<>("Invalid filter name".getBytes(), HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<byte[]> buildSuccessResponse(BufferedImage finalImage, String fileFormat) throws IOException {
        ByteArrayOutputStream imageByteStream = new ByteArrayOutputStream();
        ImageIO.write(finalImage, fileFormat, imageByteStream);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(SUPPORTED_FILE_FORMATS.get(fileFormat));
        return new ResponseEntity<>(imageByteStream.toByteArray(), headers, HttpStatus.CREATED);
    }

    @Inject
    public void setSourceUrl(@Value("${imagechallenge.sourceurl}") String theSourceUrl) {
        sourceUrl = theSourceUrl;
    }

    @Inject
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }

    @Inject
    public void setCompositeService(CompositeService compositeService) {
        this.compositeService = compositeService;
    }
}
