package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.math.Direction;
import org.togetherjava.aoc.core.math.matrix.Matrix;
import org.togetherjava.aoc.core.math.matrix.MatrixPosition;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

public class Day10 implements PuzzleSolution {
    @Override
    public Object part1(PuzzleInput input) {
        var matrix = input.toDigitMatrix();
        var trailheads = matrix.stream().filter(entry -> entry.value() == 0).toList();
        var visitedMatrix = new Matrix<Boolean>(matrix.getRows(), matrix.getCols());

        long score = 0;
        for (var trailhead : trailheads) {
            visitedMatrix.setAll(false);
            score += count1(matrix, trailhead.position(), 0, visitedMatrix);
        }

        return score;
    }

    private int count1(Matrix<Integer> matrix, MatrixPosition pos, int expectedHeight, Matrix<Boolean> visited) {
        if (matrix.outOfBounds(pos)) {
            return 0;
        }
        if (visited.get(pos)) {
            return 0;
        }
        int height = matrix.get(pos);
        if (height != expectedHeight) {
            return 0;
        }

        visited.set(pos, true);
        if (height == 9) {
            return 1;
        }

        int count = 0;
        for (var dir : Direction.getCardinal()) {
            count += count1(matrix, pos.move(dir), height + 1, visited);
        }
        visited.set(pos, false);
        return count;
    }

    @Override
    public Object part2(PuzzleInput input) {
        var matrix = input.toDigitMatrix();
        var trailheads = matrix.stream().filter(entry -> entry.value() == 0).toList();
        var visitedMatrix = new Matrix<Boolean>(matrix.getRows(), matrix.getCols());

        long rating = 0;
        for (var trailhead : trailheads) {
            visitedMatrix.setAll(false);
            rating += count2(matrix, trailhead.position(), 0, visitedMatrix);
        }

        return rating;
    }

    private int count2(Matrix<Integer> matrix, MatrixPosition pos, int expectedHeight, Matrix<Boolean> visited) {
        if (matrix.outOfBounds(pos)) {
            return 0;
        }
        if (visited.get(pos)) {
            return 0;
        }
        int height = matrix.get(pos);
        if (height != expectedHeight) {
            return 0;
        }

        if (height == 9) {
            return 1;
        }
        visited.set(pos, true);

        int count = 0;
        for (var dir : Direction.getCardinal()) {
            count += count2(matrix, pos.move(dir), height + 1, visited);
        }
        visited.set(pos, false);
        return count;
    }
}
