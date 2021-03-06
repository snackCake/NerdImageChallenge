package com.nerdery.imagechallenge.services;

import com.nerdery.imagechallenge.services.filters.FilterResult;
import com.nerdery.imagechallenge.services.filters.ImageFilter;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
@Service
public class FilterService {

    private Map<String, ImageFilter> filters;

    @Inject
    public FilterService(List<ImageFilter> filters) {
        this.filters = filters.stream().collect(Collectors.toMap(ImageFilter::getName, Function.identity()));
    }

    public Optional<FilterResult> transformImage(BufferedImage sourceImage, String filterName) {
        if (!filters.containsKey(filterName)) {
            return Optional.empty();
        } else {
            long startTime = System.currentTimeMillis();
            BufferedImage transformedImage = filters.get(filterName).transform(sourceImage);
            FilterResult result = new FilterResult(System.currentTimeMillis() - startTime, transformedImage);
            return Optional.of(result);
        }
    }
}
