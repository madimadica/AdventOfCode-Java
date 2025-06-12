package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.math.Direction;
import org.togetherjava.aoc.core.math.matrix.Matrix;
import org.togetherjava.aoc.core.math.matrix.MatrixPosition;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day15 implements PuzzleSolution {
    @Override
    public Object part1(PuzzleInput input) {
        var clusters = input.getClusters();
        var matrix = clusters.get(0).toCharMatrix();
        MatrixPosition robotPos = matrix.find('@').position();
        matrix.set(robotPos, '.');
        var moves = String.join("", clusters.get(1).getLines());
        for (var move : moves.toCharArray()) {
            Direction d = Direction.ofASCII(move);
            var previewPos = robotPos.move(d);
            var preview = matrix.get(previewPos);
            if (preview == '.') {
                robotPos = previewPos;
                continue;
            } else if (preview == '#') {
                continue;
            }
            // Try pushing
            var result = matrix.rayCastWhile(previewPos, d, e -> e.value() != '#').stream().map(Matrix.Entry::value).toList();
            if (!result.contains('.')) {
                continue;
            }
            while (matrix.get(previewPos) != '.') {
                previewPos = previewPos.move(d);
            }
            robotPos = robotPos.move(d);
            matrix.set(robotPos, '.');
            matrix.set(previewPos, 'O');
        }
        var boxes = matrix.findAll('O');
        long output = 0;
        for (var box : boxes) {
            var boxPos = box.position();
            output += (boxPos.row() * 100L) + boxPos.col();
        }
        return output;
    }

    @Override
    public Object part2(PuzzleInput input) {
        var clusters = input.getClusters();
        var matrix = PuzzleInput.of(String.join("\n", clusters.get(0).getLines().stream().map(line ->
                line.replaceAll("#", "##").replaceAll("O", "[]").replaceAll("\\.", "..").replaceAll("@", "@.")
        ).toList())).toCharMatrix();
        final var moves = String.join("", clusters.get(1).getLines());
        MatrixPosition robotPos = matrix.find('@').position(); // Track the robot position SOLELY with this variable, dont have the '@' in the matrix
        matrix.set(robotPos, '.');
        MOVEMENT_LOOP:
        for (var move : moves.toCharArray()) {
            Direction d = Direction.ofASCII(move);

//            // Debug code
//            var temp = matrix.get(robotPos);
//            matrix.set(robotPos, '@');
//            System.out.println(matrix);
//            matrix.set(robotPos, temp);
//            System.out.println("     " + move);

            var previewPos = robotPos.move(d);
            final var preview = matrix.get(previewPos);
            if (preview == '.') {
                robotPos = previewPos; // Move forward, don't push any boxes
                continue;
            } else if (preview == '#') { // Cannot move anywhere
                continue;
            }
            if (d == Direction.NORTH || d == Direction.SOUTH) { // vertical movements
                MatrixPosition lbracketInitPos = previewPos;
                MatrixPosition rbracketInitPos = previewPos;
                if (preview == '[') {
                    rbracketInitPos = previewPos.move(Direction.EAST);
                } else {
                    lbracketInitPos = previewPos.move(Direction.WEST);
                }

                record Box(MatrixPosition left, MatrixPosition right) {}
                final List<Box> allBoxes = new ArrayList<>();
                final var firstBox = new Box(lbracketInitPos, rbracketInitPos);
                allBoxes.add(firstBox);

                Set<Box> currentBoxLayer = Set.of(firstBox); // pre-emptively de-duplicate
                while (true) {
                    Set<Box> nextBoxLayer = new HashSet<>();
                    // For each box on the current layer, check the next layer for more connected boxes
                    for (Box box : currentBoxLayer) {
                        final var nextPosL = box.left.move(d);
                        final var nextPosR = box.right.move(d);
                        final var valL = matrix.get(nextPosL);
                        final var valR = matrix.get(nextPosR);
                        if (valL == '#' || valR == '#') {
                            continue MOVEMENT_LOOP; // Abort operation early, can't move anything into a wall
                        }
                        if (valL == '[' && valR == ']') {
                            // LR
                            // []   <--- add this (L, R)
                            // []
                            // @^
                            nextBoxLayer.add(new Box(nextPosL, nextPosR));
                        } else {
                            if (valL == ']') {
                                //  LR
                                // []    <--- add this shifted offset (L-1, L)
                                //  []
                                //  @^
                                nextBoxLayer.add(new Box(nextPosL.move(Direction.WEST), nextPosL));
                            }
                            if (valR == '[') {
                                //  LR
                                //   []  <--- add this shifted offset (R, R+1)
                                //  []
                                //  @^
                                nextBoxLayer.add(new Box(nextPosR, nextPosR.move(Direction.EAST)));
                            }
                        }
                    }
                    allBoxes.addAll(nextBoxLayer);
                    if (nextBoxLayer.isEmpty()) {
                        break; // Only happy case for moving all connected boxes
                    }
                    currentBoxLayer = nextBoxLayer;
                }

                // Successfully found a full-stack movement
                // For every box position, clear it
                for (Box box : allBoxes) {
                    matrix.set(box.left, '.');
                    matrix.set(box.right, '.');
                }
                // Then for every box position, set the next value in that direction
                for (Box box : allBoxes) {
                    matrix.set(box.left.move(d), '[');
                    matrix.set(box.right.move(d), ']');
                }

                // Update robot position
                robotPos = robotPos.move(d);
            } else { // horizontal
                // At this point the robot is moving into a box and wants to end up at previewPos
                var startingPos = previewPos;
                while (true) {
                    char c = matrix.get(previewPos); // can be any of: #.[]
                    if (c == '#') {
                        break; // Trying to push into a wall, can't do anything
                    } else if (c == '[' || c == ']') {
                        previewPos = previewPos.move(d); // keep scanning along the boxes
                        continue;
                    }

                    // Available space -- shift everything over once
                    var freePos = previewPos;
                    var currentPos = startingPos;
                    do {
                        matrix.set(currentPos, matrix.get(currentPos) == '[' ? ']' : '[');
                        currentPos = currentPos.move(d);
                    } while (!currentPos.equals(freePos));
                    if (d == Direction.EAST) {
                        matrix.set(freePos, ']');
                    } else {
                        matrix.set(freePos, '[');
                    }
                    matrix.set(startingPos, '.');
                    robotPos = robotPos.move(d); // Advance true robot position
                    break;
                }
            }

        }
        long output = 0;
        for (var boxCornerEntry : matrix.findAll('[')) {
            var boxPos = boxCornerEntry.position();
            output += (boxPos.row() * 100L) + boxPos.col();
        }
        return output;
    }
}
