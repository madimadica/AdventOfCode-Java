package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;
import org.togetherjava.aoc.core.utils.Counter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day22 implements PuzzleSolution {
    @Override
    public Object part1(PuzzleInput puzzleInput) {
        return puzzleInput.parseLongPerLine()
                .stream()
                .map(Day22::get2000th)
                .reduce(0L, Long::sum);
    }


    private static final int PART2_CHANGES = 2000;

    @Override
    public Object part2(PuzzleInput puzzleInput) {
        Counter<Integer> counter = new Counter<>();
        for (long num : puzzleInput.parseLongPerLine()) {
            var data = getPriceData(num);
            // Keep track of sequences we've seen. If we've seen it, we would to have had to sell it on the *first* occurrence
            Set<Integer> seen = new HashSet<>();
            for (int i = 0; i < PART2_CHANGES - 3; ++i) {
                var hashed = hashSequence(data.changes.subList(i, i + 4));
                if (!seen.add(hashed)) {
                    continue; // Would have already sold on first occurrence
                }
                counter.add(hashed, data.prices.get(i + 4));
            }
        }
        return counter.getMax().getB();
    }

    private static long mixAndPrune(long secret, long value) {
        return (secret ^ value) % 16777216;
    }

    private static long nextSecret(long s) {
        s = mixAndPrune(s, s << 6);  // multiply by 64
        s = mixAndPrune(s, s >> 5);  // divide by 32
        s = mixAndPrune(s, s << 11); // multiply by 2048
        return s;
    }

    private static long get2000th(long secret) {
        for (int i = 0; i < 2000; ++i) {
            secret = nextSecret(secret);
        }
        return secret;
    }

    private static int hashSequence(List<Integer> seq) {
        // Range of [-9, 9] offset to [1, 19] encoded as xx-xx-xx-xx
        int a = seq.get(0) + 10;
        int b = seq.get(1) + 10;
        int c = seq.get(2) + 10;
        int d = seq.get(3) + 10;
        return a + (b * 100)  + (c * 10_000) + (d * 10_000_000);
    }

    record PriceData(List<Integer> prices, List<Integer> changes) {}

    private static PriceData getPriceData(long secret) {
        List<Integer> prices = new ArrayList<>();
        List<Integer> changes = new ArrayList<>();

        for (int i = 0; i <= PART2_CHANGES; ++i) {
            int price = (int) (secret % 10);
            if (i != 0) {
                changes.add(price - prices.getLast());
            }
            prices.add(price); // Add after we compute the change
            secret = nextSecret(secret);
        }
        return new PriceData(prices, changes); // 2001 prices, 2000 changes
    }

}
