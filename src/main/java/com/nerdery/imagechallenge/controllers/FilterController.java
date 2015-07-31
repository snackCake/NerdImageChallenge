package com.nerdery.imagechallenge.controllers;

import com.nerdery.imagechallenge.services.CompositeService;
import com.nerdery.imagechallenge.services.FilterService;
import com.nerdery.imagechallenge.services.filters.FilterResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import java.util.Optional;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
@Controller
public class FilterController {

    private CompositeService compositeService;
    private FilterService filterService;
    private String sourceUrl;

    @RequestMapping("/filter/{name}")
    public ResponseEntity<byte[]> getTransformedImage(@PathVariable("name") String filterName) throws IOException, URISyntaxException {
        BufferedImage sourceImage = ImageIO.read(new URL(sourceUrl));
        Optional<FilterResult> targetImage = filterService.transformImage(sourceImage, filterName);
        if (targetImage.isPresent()) {
            return buildSuccessResponse(compositeService.buildComposite(sourceImage, targetImage.get()));
        } else {
            return new ResponseEntity<>("Invalid filter name".getBytes(), HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<byte[]> buildSuccessResponse(BufferedImage finalImage) throws IOException {
        ByteArrayOutputStream imageByteStream = new ByteArrayOutputStream();
        ImageIO.write(finalImage, "JPEG", imageByteStream);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
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
