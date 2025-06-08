package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.math.Direction;
import org.togetherjava.aoc.core.math.Distance;
import org.togetherjava.aoc.core.math.matrix.Matrix;
import org.togetherjava.aoc.core.math.matrix.MatrixPosition;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day20 implements PuzzleSolution {

    private List<MatrixPosition> getTrackPath(Matrix<Character> grid) {
        var currentPos = grid.find('S').position();
        Direction facing = grid.findAdjacent(currentPos, '.');
        List<MatrixPosition> positions = new ArrayList<>();
        while (true) {
            positions.add(currentPos);
            facing = grid.findAdjacentFacing(currentPos, '.', facing);
            if (facing == null) {
                break;
            }
            currentPos = currentPos.move(facing);
        }
        positions.add(grid.find('E').position());
        return positions;
    }


    @Override
    public Object part1(PuzzleInput input) {
        var grid = input.toCharMatrix();
        List<MatrixPosition> positions = getTrackPath(grid);
        final int len = positions.size();

        // Map position to progress on the track [0, len)
        Map<MatrixPosition, Integer> progressMap = new HashMap<>();
        for (int i = 0; i < len; ++i) {
            progressMap.put(positions.get(i), i);
        }

        long goodShortcuts = 0;
        for (int i = 0; i < len; ++i) {
            for (var endEntry : grid.scanTaxicabBorder(positions.get(i), 2)) {
                int endIndex = progressMap.getOrDefault(endEntry.position(), 0);
                if (endIndex - i - 2 >= 100) {
                    goodShortcuts++;
                }
            }
        }

        return goodShortcuts;
    }

    @Override
    public Object part2(PuzzleInput input) {
        var grid = input.toCharMatrix();
        List<MatrixPosition> positions = getTrackPath(grid);
        final int len = positions.size();

        long goodShortcuts = 0;
        for (int i = 0; i < len; ++i) {
            var shortcutStartPos = positions.get(i);

            // Check only the shortcut destinations that end at least 100 tiles further along the track
            for (int j = i + 100; j < len; ++j) {
                var shortcutEndPos = positions.get(j);
                int dist = Distance.taxicab(shortcutStartPos, shortcutEndPos);
                if (dist < 2 || 20 < dist) {
                    continue;
                }
                if (j - i - dist >= 100) {
                    goodShortcuts++;
                }
            }
        }
        return goodShortcuts;
    }

    // Initial, suboptimal version that naively checked each tile's full search space
//    @Override
//    public Object part2(PuzzleInput input) {
//        var grid = input.toCharMatrix();
//        List<MatrixPosition> positions = getTrackPath(grid);
//        final int len = positions.size();
//
//        // Map position to progress on the track [0, len)
//        Map<MatrixPosition, Integer> progressMap = new HashMap<>();
//        for (int i = 0; i < len; ++i) {
//            progressMap.put(positions.get(i), i);
//        }
//
//        long goodShortcuts = 0;
//        for (int i = 0; i < len; ++i) {
//            var startPos = positions.get(i);
//            for (var endEntry : grid.scanTaxicabArea(startPos, 20)) {
//                var endPos = endEntry.position();
//                int shortcutDuration = Distance.taxicab(startPos, endPos);
//                int endIndex = progressMap.getOrDefault(endPos, 0);
//                if (endIndex - i - shortcutDuration >= 100) {
//                    goodShortcuts++;
//                }
//            }
//        }
//        return goodShortcuts;
//    }
}
