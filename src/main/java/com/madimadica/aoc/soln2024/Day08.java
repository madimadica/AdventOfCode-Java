package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.math.Combinatorics;
import org.togetherjava.aoc.core.math.matrix.Matrix;
import org.togetherjava.aoc.core.math.matrix.MatrixPosition;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Day08 implements PuzzleSolution {

    @Override
    public Object part1(PuzzleInput input) {
        var matrix = input.toCharMatrix();
        var antennas = matrix.stream().filter(entry -> entry.value() != '.').toList();
        var groups = antennas.stream().collect(Collectors.groupingBy(Matrix.Entry::value));

        Set<MatrixPosition> locations = new HashSet<>();
        for (var groupEntry : groups.entrySet()) {
            var groupNodes = groupEntry.getValue();
            var arrangements = Combinatorics.arrange(groupNodes, 2);
            for (var arr: arrangements) {
                var left = arr.get(0);
                var right = arr.get(1);
                var lPos = left.position();
                var rPos = right.position();
                // Signs invert nicely if the Left is actually the Right
                var rowDiff = lPos.row() - rPos.row();
                var colDif = lPos.col() - rPos.col();
                var x1 = lPos.move(rowDiff, colDif);
                var x2 = rPos.move(-rowDiff, -colDif);
                if (matrix.inBounds(x1)) {
                    locations.add(x1);
                }
                if (matrix.inBounds(x2)) {
                    locations.add(x2);
                }
            }
        }
        return locations.size();
    }

    @Override
    public Object part2(PuzzleInput input) {
        var matrix = input.toCharMatrix();
        var antennas = matrix.stream().filter(entry -> entry.value() != '.').toList();
        var groups = antennas.stream().collect(Collectors.groupingBy(Matrix.Entry::value));

        Set<MatrixPosition> locations = new HashSet<>();
        for (var groupEntry : groups.entrySet()) {
            var groupNodes = groupEntry.getValue();
            var arrangements = Combinatorics.arrange(groupNodes, 2);
            for (var arr: arrangements) {
                var left = arr.get(0);
                var right = arr.get(1);
                var lPos = left.position();
                var rPos = right.position();
                locations.add(lPos);
                locations.add(rPos);
                var rowDiff = lPos.row() - rPos.row();
                var colDif = lPos.col() - rPos.col();
                var x1 = lPos.move(rowDiff, colDif);
                var x2 = rPos.move(-rowDiff, -colDif);
                while (matrix.inBounds(x1)) {
                    locations.add(x1);
                    x1 = x1.move(rowDiff, colDif);
                }
                while (matrix.inBounds(x2)) {
                    locations.add(x2);
                    x2 = x2.move(-rowDiff, -colDif);
                }
            }
        }
        return locations.size();
    }

}
