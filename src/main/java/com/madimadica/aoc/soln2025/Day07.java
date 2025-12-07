package com.madimadica.aoc.soln2025;

import org.togetherjava.aoc.core.math.matrix.MatrixPosition;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.*;

import static org.togetherjava.aoc.core.math.Direction.SOUTH;
import static org.togetherjava.aoc.core.math.Direction.EAST;
import static org.togetherjava.aoc.core.math.Direction.WEST;

public class Day07 implements PuzzleSolution {
    private static final char SPLITTER = '^';
    @Override
    public Object part1(PuzzleInput puzzleInput) {
        var matrix = puzzleInput.toCharMatrix();
        var startPos = matrix.find('S').position();
        List<MatrixPosition> currentBeams = new ArrayList<>();
        currentBeams.add(startPos.move(SOUTH));
        int currentRow = 1;
        int totalSplits = 0;
        for (int r = currentRow; r < matrix.getRows() - 1; ++r) {
            Set<MatrixPosition> nextBeams = new HashSet<>();
            for (var beam : currentBeams) {
                var belowPos = beam.move(SOUTH);
                var belowChar = matrix.get(belowPos);
                if (belowChar == SPLITTER) {
                    totalSplits++;
                    nextBeams.add(belowPos.move(EAST));
                    nextBeams.add(belowPos.move(WEST));
                } else {
                    nextBeams.add(belowPos);
                }
            }
            currentBeams = new ArrayList<>(nextBeams);
        }
        return totalSplits;
    }

    @Override
    public Object part2(PuzzleInput puzzleInput) {
        var matrix = puzzleInput.toCharMatrix();
        var startPos = matrix.find('S').position();
        List<QuantumPosition> currentBeams = new ArrayList<>();
        currentBeams.add(new QuantumPosition(startPos.move(SOUTH), 1));
        int currentRow = 1;
        for (int r = currentRow; r < matrix.getRows() - 1; ++r) {
            List<QuantumPosition> nextBeams = new ArrayList<>();
            for (var beam : currentBeams) {
                var belowPos = beam.pos.move(SOUTH);
                var belowChar = matrix.get(belowPos);
                if (belowChar == SPLITTER) {
                    nextBeams.add(new QuantumPosition(belowPos.move(EAST), beam.timelines));
                    nextBeams.add(new QuantumPosition(belowPos.move(WEST), beam.timelines));
                } else {
                    nextBeams.add(new QuantumPosition(belowPos, beam.timelines));
                }
            }
            // need to combine beams, almost like memoizing
            Map<MatrixPosition, Long> map = new HashMap<>();
            for (var beam : nextBeams) {
                Long value = map.getOrDefault(beam.pos, 0L);
                map.put(beam.pos, value + beam.timelines);
            }
            currentBeams = new ArrayList<>();
            for (var entry : map.entrySet()) {
                currentBeams.add(new QuantumPosition(entry.getKey(), entry.getValue()));
            }
        }
        long total = 0;
        for (var x : currentBeams) {
            total += x.timelines;
        }
        return total;
    }

    record QuantumPosition(MatrixPosition pos, long timelines) {

    }
}
