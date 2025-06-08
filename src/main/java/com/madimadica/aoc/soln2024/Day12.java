package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.math.Direction;
import org.togetherjava.aoc.core.math.matrix.Matrix;
import org.togetherjava.aoc.core.math.matrix.MatrixPosition;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;
import org.togetherjava.aoc.core.utils.Triple;
import org.togetherjava.aoc.core.utils.Tuple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day12 implements PuzzleSolution {
    @Override
    public Object part1(PuzzleInput input) {
        var charMatrix = input.toCharMatrix();
        var seenMatrix = new Matrix<Boolean>(charMatrix.getRows(), charMatrix.getCols());
        seenMatrix.setAll(false);
        long total = 0;
        for (var entry : charMatrix.getEntries()) {
            List<MatrixPosition> islandPositions = new ArrayList<>();
            findRegion(entry.value(), entry.position(), charMatrix, seenMatrix, islandPositions);
            long area = islandPositions.size();
            long perimeter = islandPerimeter(entry.value(), charMatrix, islandPositions);
            long temp = area * perimeter;
            total += temp;
        }
        return total;
    }

    public void findRegion(char type, MatrixPosition pos, Matrix<Character> matrix, Matrix<Boolean> seen, List<MatrixPosition> positions) {
        if (matrix.outOfBounds(pos)) {
            return;
        }
        if (matrix.get(pos) != type) {
            return;
        }
        if (seen.get(pos)) {
            return;
        }
        seen.set(pos, true);
        positions.add(pos);
        for (Direction dir : Direction.getCardinal()) {
            findRegion(type, pos.move(dir), matrix, seen, positions);
        }
    }

    public int islandPerimeter(char islandType, Matrix<Character> matrix, List<MatrixPosition> islandPositions) {
        int perimeter = 0;
        for (var pos :  islandPositions) {
            for (Direction dir : Direction.getCardinal()) {
                var $type = matrix.tryGet(pos.move(dir));
                if ($type.isEmpty() || $type.get() != islandType) {
                    perimeter++;
                }
            }
        }
        return perimeter;
    }

    @Override
    public Object part2(PuzzleInput input) {
        var charMatrix = input.toCharMatrix();
        var seenMatrix = new Matrix<Boolean>(charMatrix.getRows(), charMatrix.getCols());
        seenMatrix.setAll(false);

        var cornerSet = corners(charMatrix);

        long total = 0;
        for (var entry : charMatrix.getEntries()) {
            List<MatrixPosition> islandPositions = new ArrayList<>();
            findRegion(entry.value(), entry.position(), charMatrix, seenMatrix, islandPositions);
            long area = islandPositions.size();
            long sides = getSides(entry.value(), islandPositions, cornerSet);
            long temp = area * sides;
            total += temp;
        }
        return total;
    }

    public Set<Triple<MatrixPosition, MatrixPosition, Boolean>> boundaries(Matrix<Character> matrix) {
        Set<Triple<MatrixPosition, MatrixPosition, Boolean>> result = new HashSet<>();
        for (var entry : matrix.getEntries()) {
            char self = entry.value();
            for (Direction dir : Direction.getCardinal()) {
                var adjPos = entry.position().move(dir);
                var $adj = matrix.tryGet(adjPos);
                boolean isBoundary = $adj.isEmpty() || $adj.get() != self;
                result.add(new Triple<>(entry.position(), adjPos, isBoundary));
            }
        }
        return result;
    }

    public int getSides(Character ch, List<MatrixPosition> positions, Set<Triple<MatrixPosition, MatrixPosition, Character>> corners) {
        Set<Triple<MatrixPosition, MatrixPosition, Character>> myCorners = new HashSet<>();
        for (var pos : positions) {
            var topLeft = pos;
            var topRight = pos.move(Direction.EAST);
            var bottomRight = topRight.move(Direction.SOUTH);
            var bottomLeft = topLeft.move(Direction.SOUTH);
            for (var cornerPos : List.of(topLeft, topRight, bottomLeft, bottomRight)) {
                var tuple = new Triple<>(cornerPos, pos, ch);

                if (corners.contains(tuple)) {
                    myCorners.add(tuple);
                }
            }
        }
        return myCorners.size();
    }

    // Set of corners, starting pos, and their owning chartype
    public Set<Triple<MatrixPosition, MatrixPosition, Character>> corners(Matrix<Character> matrix) {
        var boundaries = boundaries(matrix);
        List<Tuple<Direction, Direction>> cornerDirections = List.of(
                new Tuple<>(Direction.NORTH, Direction.EAST),
                new Tuple<>(Direction.NORTH, Direction.WEST),
                new Tuple<>(Direction.SOUTH, Direction.EAST),
                new Tuple<>(Direction.SOUTH, Direction.WEST)
        );
        // top left aligned
        Set<Triple<MatrixPosition, MatrixPosition, Character>> corners = new HashSet<>();
        for (var entry : matrix.getEntries()) {
            var pos = entry.position();
            for (var cDir : cornerDirections) {
                boolean hasA = boundaries.contains(new Triple<>(pos, pos.move(cDir.getA()), true));
                boolean hasB = boundaries.contains(new Triple<>(pos, pos.move(cDir.getB()), true));
                var cornerPos = pos;
                if (cDir.getA() == Direction.SOUTH) {
                    cornerPos = cornerPos.move(Direction.SOUTH);
                }
                if (cDir.getB() == Direction.EAST) {
                    cornerPos = cornerPos.move(Direction.EAST);
                }
                if (hasA && hasB) {
                    corners.add(new Triple<>(cornerPos, pos, entry.value()));
                }
                if (!hasA && !hasB) {
                    var diagonal = pos.move(cDir.getA()).move(cDir.getB());
                    if (matrix.get(diagonal) != entry.value()) {
                        corners.add(new Triple<>(cornerPos, pos, entry.value()));
                    }
                }
            }
        }
        return corners;
    }
}
