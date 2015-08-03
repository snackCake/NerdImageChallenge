package com.nerdery.imagechallenge.genetic;

import java.security.SecureRandom;
import java.util.stream.IntStream;

public class Crossover {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final double CROSSOVER_RATE = 0.7;

    private Crossover() {}

    /**
     * Attempt to perform crossover based on randomness - the crossover rate determines how likely we are.
     *
     * @param c1 The first candidate.
     * @param c2 The second candidate.
     * @return Whether or not crossover was performed.
     */
    public static boolean attemptCrossover(final Candidate c1, final Candidate c2) {
        if (RANDOM.nextFloat() > CROSSOVER_RATE) {
            performCrossover(c1, c2);
            return true;
        }

        return false;
    }

    /**
     * Given two candidates, perform crossover at a random point within their data.
     *
     * NOTE: I'm assuming that c1 and c2 have equal sizes on purpose. We should only be doing this on two members of
     * the same population.
     *
     * @param c1 The first candidate.
     * @param c2 The second candidate.
     */
    public static void performCrossover(final Candidate c1, final Candidate c2) {
        final int mark = RANDOM.nextInt(c1.getSize());
        IntStream.range(mark, c1.getSize()).forEach(i -> c1.swapPixel(i, c2));
    }
}
