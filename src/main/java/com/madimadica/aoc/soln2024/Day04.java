package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.math.Combinatorics;
import org.togetherjava.aoc.core.math.Direction;
import org.togetherjava.aoc.core.math.matrix.Matrix;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;
import org.togetherjava.aoc.core.utils.StringUtils;

import java.util.List;

public class Day04 implements PuzzleSolution {

    @Override
    public Object part1(PuzzleInput input) {
        var charMatrix = input.toCharMatrix();
        List<Character> XMAS = List.of('X', 'M', 'A', 'S');
        int count = 0;
        for (var entry : charMatrix.getEntries()) {
            var pos = entry.position();
            for (var direction : Direction.getAll()) {
                List<Character> projection = charMatrix.rayCast(pos, direction, 4)
                        .stream()
                        .map(Matrix.Entry::value)
                        .toList();
                if (projection.equals(XMAS)) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public Object part2(PuzzleInput input) {
        var charMatrix = input.toCharMatrix();
        List<String> permutations = Combinatorics.getRotations("SSMM");
        return charMatrix.stream().filter((entry) -> {
            if (entry.value() == 'A') {
                var corners = charMatrix.getOuterBorder(entry.position()).getCorners();
                String current = StringUtils.joinChars(corners);
                return permutations.stream().anyMatch(current::equals);
            }
            return false;
        }).count();
    }
}
