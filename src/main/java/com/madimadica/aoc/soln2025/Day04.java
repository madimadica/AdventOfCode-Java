package com.madimadica.aoc.soln2025;

import org.togetherjava.aoc.core.math.matrix.MatrixBorder;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.ArrayList;
import java.util.List;

public class Day04 implements PuzzleSolution {
    @Override
    public Object part1(PuzzleInput puzzleInput) {
        long total = 0;
        var matrix = puzzleInput.toCharMatrix();
        for (var entry : matrix.getEntries()) {
            if (entry.value() != '@') {
                continue; // nothing to remove
            }
            var border = borderToList(matrix.getOuterBorder(entry.row(), entry.col()));
            if (border.stream().filter(e -> e.equals('@')).count() < 4) {
                total++;
            }
        }
        return total;
    }

    @Override
    public Object part2(PuzzleInput puzzleInput) {
        long total = 0;
        var matrix = puzzleInput.toCharMatrix().toBinaryMatrix('@');
        while (true) {
            final int before = matrix.countOnes();
            for (int r = 0; r < matrix.getRows(); ++r) {
                for (int c = 0; c < matrix.getCols(); ++c) {
                    if (!matrix.get(r, c)) {
                        continue; // nothing to remove
                    }
                    var borderTiles = borderToList(matrix.getOuterBorder(r, c));
                    int count = 0;
                    for (var t : borderTiles) {
                        if (t) {
                            count++;
                        }
                    }
                    boolean canRemove = count < 4;
                    if (canRemove) {
                        matrix.set(r, c, false);
                        total++;
                    }
                }
            }
            int after = matrix.countOnes();
            if (before == after) { // Go until no differences
                break;
            }
        }
        return total;
    }

    private static <T> List<T> borderToList(MatrixBorder<T> border) {
        var sides = border.getAllSides();
        var corners = border.getCornersNonNull();
        var temp = new ArrayList<>(sides);
        temp.addAll(corners);
        return temp;
    }
}
