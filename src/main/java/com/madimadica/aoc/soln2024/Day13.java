package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.Regex;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.List;
import java.util.function.Function;

public class Day13 implements PuzzleSolution {

    @Override
    public Object part1(PuzzleInput input) {
        long total = 0;
        for (var machine : input.getClusters()) {
            total += getTokensNeeded(machine, Coord::of);
        }
        return total;
    }

    @Override
    public Object part2(PuzzleInput input) {
        long total = 0;
        for (var machine : input.getClusters()) {
            total += getTokensNeeded(machine, Coord::ofAdjusted);
        }
        return total;
    }

    record Coord(long x, long y) {
        public static Coord of(List<Integer> nums) {
            return new Coord(nums.get(0), nums.get(1));
        }
        public static Coord ofAdjusted(List<Integer> nums) {
            return new Coord(nums.get(0) + 10000000000000L, nums.get(1) + 10000000000000L);
        }
    }

    private long getTokensNeeded(PuzzleInput input, Function<List<Integer>, Coord> prizeMapper) {
        var lines = input.getLines();
        var aCoord = Coord.of(Regex.parseInts(lines.get(0))); // 3 tokens
        var bCoord = Coord.of(Regex.parseInts(lines.get(1))); // 1 token
        var p = prizeMapper.apply(Regex.parseInts(lines.get(2)));

        /*
         * Given:
         * aAx + bBx = Px
         * aAy + bBy = Py
         * where a and b are unknown, we get the matrix
         * Ax Bx | Px
         * Ay By | Py
         * Solve with A^{-1}b = x
         */
        long a = aCoord.x;
        long b = bCoord.x;
        long c = aCoord.y;
        long d = bCoord.y;

        long detA = (a * d) - (b * c);

        if (detA == 0) {
            return 0; // absolutely no solutions, prevent div by 0
        }

        // A inverse = 1/det(A) * [[d, -a12] [-c, a11]]
        // A inverse [px, py] = x

        long solnA = Math.floorDiv(d * p.x - b * p.y, detA);
        long solnB = Math.floorDiv(-c * p.x + a * p.y, detA);

        // check soln, still no solution for non-whole (rounded) tokens
        long outX = aCoord.x * solnA + bCoord.x * solnB;
        long outY = aCoord.y * solnA + bCoord.y * solnB;
        Coord out = new Coord(outX, outY);
        if (!p.equals(out)) {
            return 0;
        } else {
            return solnA * 3 + solnB;
        }
    }

}
