package com.nerdery.imagechallenge.services.filters;

import com.nerdery.imagechallenge.genetic.Candidate;
import com.nerdery.imagechallenge.genetic.Population;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrick Garrity
 *
 * Filter implementation that takes a random filter and attempts to evolve it from the base image.
 *
 * TODO: Support the following operations:
 * - All known filters
 * - Specific filter
 */
@Component
public class GeneticFilter implements ImageFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneticFilter.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final Map<String, ImageFilter> FILTERS = new HashMap<>();

    private static final int POPULATION_SIZE = 50;
    private static final int GENERATIONS = 800;

    /**
     * When we load this class we want to use reflection to find every other image filter and attempt to instantiate
     * those filters.
     */
    static {
        final Reflections reflections = new Reflections("com.nerdery.imagechallenge.services.filters");
        reflections.getSubTypesOf(ImageFilter.class)
                .stream()
                .filter(c -> c.getAnnotation(Component.class) != null)
                .filter(c -> c != GeneticFilter.class)
                .map(c -> {
                    try {
                        return c.newInstance();
                    } catch (final Exception e) {
                        LOGGER.warn("Unable to instantiate the filter class {}", c.getName());
                        return null;
                    }
                })
                .filter(imageFilter -> imageFilter != null)
                .forEach(imageFilter -> FILTERS.put(imageFilter.getName(), imageFilter));

        LOGGER.info("Successfully loaded {} filters.", FILTERS.size());
        FILTERS.forEach((k, v) -> LOGGER.info("Loaded filter: {}", k));
    }

    public GeneticFilter() {}

    @Override
    public String getName() {
        return "genetic";
    }

    @Override
    public BufferedImage transform(BufferedImage sourceImage) {
        final ImageFilter filter = chooseRandomFilter();
        LOGGER.info("Selected filter {}", filter.getName());

        final BufferedImage referenceImage = filter.transform(sourceImage);
        LOGGER.info("Reference image has {} width and {} height", referenceImage.getWidth(), referenceImage.getHeight());

        final int [] reference = getImageData(referenceImage);

        LOGGER.info("Creating a new population of size {}", POPULATION_SIZE);
        final Population population = new Population(POPULATION_SIZE);
        population.resetPopulation(getImageData(sourceImage));

        LOGGER.info("Executing {} generations of evolution", GENERATIONS);
        for (int i = 0; i < GENERATIONS; i++) {
            population.generation(reference);
            LOGGER.info("Completed generation {}", i);
        }

        final Candidate selectedCandidate = population.topScore();
        LOGGER.info("Selected candidate with score {} -- the goal score is 0.", selectedCandidate.getTotalScore());

        final BufferedImage targetImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), sourceImage.getType());
        setImageData(targetImage, selectedCandidate.getData());

        return targetImage;
    }

    private ImageFilter chooseRandomFilter() {
        final int choice = RANDOM.nextInt(FILTERS.size());
        int i = 0;
        for (Map.Entry<String, ImageFilter> entry : FILTERS.entrySet()) {
            if (i == choice) {
                return entry.getValue();
            }
            else {
                i++;
            }
        }

        throw new IllegalArgumentException("Invalid random filter selected.");
    }

    private int[] getImageData(final BufferedImage image) {
        final int[] data = new int[image.getWidth() * image.getHeight()];
        int i = 0;
        for (int row = 0; row < image.getHeight(); row++) {
            for (int col = 0; col < image.getWidth(); col++) {
                data[i] = image.getRGB(col, row);
                i++;
            }
        }
        return data;
    }

    private void setImageData(final BufferedImage image, final int[] data) {
        int i = 0;
        for (int row = 0; row < image.getHeight(); row++) {
            for (int col = 0; col < image.getWidth(); col++) {
                image.setRGB(col, row, data[i]);
                i++;
            }
        }
    }
}
