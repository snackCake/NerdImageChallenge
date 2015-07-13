package com.nerdery.imagechallenge.genetic;

import java.security.SecureRandom;
import java.util.stream.IntStream;

public class Mutation {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final double MUTATION_RATE = 0.01;

    private Mutation() {}

    public static void mutate(final Candidate candidate) {
        IntStream.range(0, candidate.getSize()).forEach(i -> mutatePixel(i, candidate));
    }

    /**
     * Attempt to mutate a specific pixel. Only non-perfect pixels may be mutated. The mutation rate controls the
     * probability that the pixel will be mutated. Mutation is defined as random jitter.
     *
     * @param index The pixel index.
     * @param candidate The candidate.
     */
    private static void mutatePixel(final int index, final Candidate candidate) {
        if (candidate.getScore(index) > 0) {
            if (RANDOM.nextDouble() < MUTATION_RATE) {
                candidate.setPixel(index, Colors.jitterPixelColors(candidate.getPixel(index)));
            }
        }
    }
}
