package com.madimadica.aoc.soln2025;

import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Day03 implements PuzzleSolution {
    @Override
    public Object part1(PuzzleInput puzzleInput) {
        long total = 0;
        for (String s : puzzleInput.getLines()) {
            int max = 0;
            var splits = s.split("");
            List<Integer> x = Stream.of(splits).map(Integer::parseInt).toList();
            int len = x.size();
            for (int i = 0; i < len; ++i) {
                for (int j = i + 1; j < len; ++j) {
                    int value = x.get(i) * 10 + x.get(j);
                    max = Math.max(max, value);
                }
            }
            total += max;
        }
        return total;
    }

    @Override
    public Object part2(PuzzleInput puzzleInput) {
        // within the remaining window range, find the largest digit
        long total = 0;
        for (String s : puzzleInput.getLines()) {
            var splits = s.split("");
            List<Integer> digits = Stream.of(splits).map(Integer::parseInt).toList();
            List<Integer> choices = new ArrayList<>();

            int startFrom = 0;
            for (int choiceNumber = 0; choiceNumber < 12; ++choiceNumber) {
                int endAt = digits.size() - (12 - choices.size());
                int currentMax = 0;
                int maxIndex = -1;
                for (int i = startFrom; i <= endAt; ++i) {
                    var digit = digits.get(i);
                    if (digit > currentMax) {
                        currentMax = digit;
                        maxIndex = i;
                    }
                }
                choices.add(currentMax);
                startFrom = maxIndex + 1;
            }

            long joined = Long.parseLong(String.join("", choices.stream().map(Object::toString).toList()));
            total += joined;
        }
        return total;
    }
}
