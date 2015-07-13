package com.nerdery.imagechallenge.genetic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

public class Candidate {
    private static final Logger LOGGER = LoggerFactory.getLogger(Candidate.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final int[] data;
    private final int[] scores;
    private int totalScore = Integer.MAX_VALUE;
    private int size;

    protected Candidate(final int [] reference) {
        data = Arrays.copyOf(reference, reference.length);
        scores = new int[reference.length];
        this.size = reference.length;
    }

    public static Candidate fromReference(final int[] reference) {
        final Candidate candidate = new Candidate(reference);
        candidate.randomDelta();
        return candidate;
    }

    public int getScore(final int index) {
        return scores[index];
    }

    public int getPixel(final int index) {
        return data[index];
    }

    public void setPixel(final int index, final int value) {
        data[index] = value;
    }

    /**
     * Swap a single pixel with another candidate.
     *
     * @param index The index of the pixel to swap.
     * @param other The other candidate.
     */
    public void swapPixel(final int index, final Candidate other) {
        final int value = data[index];
        data[index] = other.getPixel(index);
        other.setPixel(index, value);
    }

    /**
     * Randomize all of the pixels in the data.
     */
    protected void randomDelta() {
        IntStream.range(0, size).forEach(i -> data[i] = Colors.jitterPixelColors(data[i]));
    }

    /**
     * Calculate the current score of this candidate based on the given reference data.
     *
     * @param reference The reference data.
     */
    public void calculateScore(final int[] reference) {
        IntStream.range(0, size).forEach(i -> scores[i] = calculateScore(i, reference));
        totalScore = IntStream.of(scores).sum();
    }

    /**
     * Calculate the score of an individual pixel. The pixel score is equivalent to the distance between pixel values.
     *
     * Lower scores are better.
     *
     * @param index The pixel index.
     * @param reference The reference data.
     * @return The pixel score.
     */
    protected int calculateScore(final int index, final int[] reference) {
        return Math.abs(reference[index] - data[index]);
    }

    public int[] getData() {
        return data;
    }

    public int[] getScores() {
        return scores;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getSize() {
        return size;
    }
}
