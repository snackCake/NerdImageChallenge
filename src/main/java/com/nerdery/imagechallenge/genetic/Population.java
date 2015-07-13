package com.nerdery.imagechallenge.genetic;

import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Population {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOURNAMENT_SIZE = 4;

    private Candidate[] candidates;
    private int size;

    public Population(final int size) {
        this.size = size;
        candidates = new Candidate[size];
    }

    public Candidate topScore() {
        Candidate best = null;
        for (final Candidate candidate : candidates) {
            if (best == null) {
                best = candidate;
            }
            else {
                if (candidate.getTotalScore() < best.getTotalScore()) {
                    best = candidate;
                }
            }
        }

        return best;
    }

    public void resetPopulation(final int[] reference) {
        IntStream.range(0, size).forEach(i -> candidates[i] = Candidate.fromReference(reference));
    }

    public void generation(final int[] reference) {
        calculateAllScores(reference);

        final LinkedList<Candidate> nextGeneration = new LinkedList<>();

        IntStream.range(0, size / 2).parallel().forEach(i -> {
            final Candidate c1 = tournament();
            final Candidate c2 = tournament();

            Crossover.attemptCrossover(c1, c2);
            Mutation.mutate(c1);
            Mutation.mutate(c2);
            nextGeneration.add(c1);
            nextGeneration.add(c2);
        });

        nextGeneration.toArray(candidates);
    }

    private Candidate selectRandomCandidate() {
        return candidates[RANDOM.nextInt(size)];
    }

    private void calculateAllScores(final int[] reference) {
        Stream.of(candidates).forEach(c -> c.calculateScore(reference));
    }

    private Candidate tournament() {
        Candidate best = null;
        Candidate current;

        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            current = selectRandomCandidate();
            if (best == null) {
                best = current;
            }
            else {
                if (current.getTotalScore() < best.getTotalScore()) {
                    best = current;
                }
            }
        }

        return best;
    }
}
