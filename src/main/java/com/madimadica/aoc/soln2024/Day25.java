package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.math.matrix.BinaryMatrix;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.*;

public class Day25 implements PuzzleSolution {


    @Override
    public Object part1(PuzzleInput input) {
        List<BinaryMatrix> all = new ArrayList<>();
        for (var cluster : input.getClusters()) {
            all.add(cluster.toCharMatrix().toBinaryMatrix('#'));
        }
        var locks = all.stream().filter(m -> m.get(0, 0)).toList();
        var keys  = all.stream().filter(m -> !m.get(0, 0)).toList();
        long answer = 0;
        for (var lock : locks) {
            for (var key : keys) {
                if (lock.and(key).allFalse()) {
                    answer++;
                }
            }
        }
        return answer;
    }

    @Override
    public Object part2(PuzzleInput input) {
        return null;
    }
}
