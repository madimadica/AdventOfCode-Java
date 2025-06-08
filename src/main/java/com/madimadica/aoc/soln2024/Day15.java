package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.math.Direction;
import org.togetherjava.aoc.core.math.matrix.Matrix;
import org.togetherjava.aoc.core.math.matrix.MatrixPosition;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

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
        var moves = String.join("", clusters.get(1).getLines());
        MatrixPosition robotPos = matrix.find('@').position();
        int i = 0;
        matrix.set(robotPos, '.');
        for (var move : moves.toCharArray()) {
            if (i++ == 21) {
                System.out.print("");
            }
            Direction d = Direction.ofASCII(move);
            var temp = matrix.get(robotPos);
            matrix.set(robotPos, '@');
            System.out.println(matrix);
            matrix.set(robotPos, temp);

            var previewPos = robotPos.move(d);
            var preview = matrix.get(previewPos);
            if (preview == '.') {
                robotPos = previewPos;
                System.out.println("     " + move);
                continue;
            } else if (preview == '#') {
                System.out.println("     " + move);
                continue;
            }
            boolean isVertical = d == Direction.NORTH || d == Direction.SOUTH;
            var originalPreviewPos = previewPos.move(0, 0);
            if (isVertical) {
                MatrixPosition lbracketInitPos;
                MatrixPosition rbracketInitPos;
                if (d == Direction.NORTH) {
                    if (preview == '[') {
                        lbracketInitPos = previewPos.move(0, 0);
                        rbracketInitPos = previewPos.move(Direction.EAST);
                    } else {
                        lbracketInitPos = previewPos.move(Direction.WEST);
                        rbracketInitPos = previewPos.move(0, 0);
                    }
                } else {
                    if (preview == '[') {
                        lbracketInitPos = previewPos.move(0, 0);
                        rbracketInitPos = previewPos.move(Direction.EAST);
                    } else {
                        lbracketInitPos = previewPos.move(Direction.WEST);
                        rbracketInitPos = previewPos.move(0, 0);
                    }
                }
                MatrixPosition lbracketCurPos = lbracketInitPos.move(0, 0);
                MatrixPosition rbracketCurPos = rbracketInitPos.move(0, 0);
                while (true) {
                    var left = matrix.get(lbracketCurPos);
                    var right = matrix.get(rbracketCurPos);
                    // [#
                    String s = "" + left + "" + right;
                    if (s.equals("[]")) {
                        lbracketCurPos = lbracketCurPos.move(d);
                        rbracketCurPos = rbracketCurPos.move(d);
                        continue;
                    }
                    if (s.equals("..")) {
                        // can move
                        matrix.set(lbracketInitPos, '.');
                        matrix.set(rbracketInitPos, '.');
                        matrix.set(lbracketCurPos, '[');
                        matrix.set(rbracketCurPos, ']');
                        robotPos = robotPos.move(d);
                        break;
                    } else {
                        break;
                    }
                }
            } else {
                var startingPos = previewPos.move(0, 0);
                while (true) {
                    char c = matrix.get(previewPos);
                    if (c == '.') {
                        // can move all
                        var currentPos = startingPos;
                        do {
                            var currentVal = matrix.get(currentPos);
                            matrix.set(currentPos, currentVal == '[' ? ']' : '[');
                            currentPos = currentPos.move(d);
                        } while (!currentPos.equals(previewPos));
                        if (d == Direction.EAST) {
                            matrix.set(previewPos, ']');
                        } else {
                            matrix.set(previewPos, '[');
                        }
                        matrix.set(startingPos, '.');
                        robotPos = robotPos.move(d);
                        break;
                    } else if (c == '#') {
                        break;
                    }
                    previewPos = previewPos.move(d);
                }
            }
            System.out.println("     " + move);

        }
        // TODO update the math
        var boxes = matrix.findAll('[');
        long output = 0;
        for (var box : boxes) {
            var boxPos = box.position();
            output += (boxPos.row() * 100L) + boxPos.col();
        }
//        return output;
        return null; // TODO FIX PART 2
    }
}
