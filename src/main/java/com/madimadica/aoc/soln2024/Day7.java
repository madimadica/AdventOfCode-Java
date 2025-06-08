package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.math.Combinatorics;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.List;

public class Day7 implements PuzzleSolution {

    @Override
    public Object part1(PuzzleInput input) {
        return runWith(input, List.of('+', '*'));
    }

    @Override
    public Object part2(PuzzleInput input) {
        return runWith(input, List.of('+', '*', '|'));
    }

    private long runWith(PuzzleInput input, final List<Character> operators) {
        long answer = 0;
        for (List<Long> line : input.parseNumbers()) {
            final long expected = line.get(0);
            List<Long> operands = line.subList(1, line.size());
            var permutations = Combinatorics.arrangeWithRepetition(operands.size() - 1, operators);
            for (List<Character> permutation : permutations) {
                long result = evaluate(operands, permutation);
                if (result == expected) {
                    answer += result;
                    break;
                }
            }
        }
        return answer;
    }

    private long evaluate(List<Long> operands, List<Character> operators) {
        long prev = operands.get(0);
        for (int i = 1; i < operands.size(); ++i) {
            long current = operands.get(i);
            switch (operators.get(i - 1)) {
                case '+' -> prev += current;
                case '*' -> prev *= current;
                case '|' -> prev = Long.parseLong(prev + "" + current);
            }
        }
        return prev;
    }

}
