package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.math.Direction;
import org.togetherjava.aoc.core.math.matrix.BinaryMatrix;
import org.togetherjava.aoc.core.math.matrix.Matrix;
import org.togetherjava.aoc.core.math.matrix.MatrixPosition;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @see Day6Animation
 */
public class Day6Animated implements PuzzleSolution {
    Day6Animation animation = new Day6Animation();

    @Override
    public Object part1(PuzzleInput input) {
        var matrix = input.toCharMatrix();
        matrix.findAll('#').forEach(
                entry -> animation.placeWallAt(entry.position())
        );

        MatrixPosition currentPosition = matrix.find('^').position();
        animation.start();

        Direction facing = Direction.NORTH;
        BinaryMatrix visited = new BinaryMatrix(matrix);
        while (true) {
            animation.moveTo(currentPosition);
            visited.set(currentPosition, true);
            MatrixPosition nextPosition = currentPosition.move(facing);
            if (matrix.outOfBounds(nextPosition)) {
                break;
            }
            while (matrix.get(nextPosition) == '#') {
                facing = facing.rotateRight();
                nextPosition = currentPosition.move(facing);
            }
            currentPosition = nextPosition;
        }
        return visited.countOnes();
    }

    @Override
    public Object part2(PuzzleInput input) {
        var matrix = input.toCharMatrix();
        MatrixPosition startingPosition = matrix.find('^').position();
        int combinations = 0;

        List<MatrixPosition> interrupts = getPath(input);
        for (MatrixPosition interrupt : interrupts) {
            var bruteMatrix = input.toCharMatrix();
            animation.reset(false);
            matrix.findAll('#').forEach(
                    entry -> animation.placeWallAt(entry.position())
            );
            bruteMatrix.set(interrupt, '#');
            animation.setColor(interrupt, Color.WHITE.getRGB());
            animation.repaint();

            Set<MatrixPosition> cornersVisited = new HashSet<>();
            MatrixPosition currentPosition = startingPosition;
            Direction facing = Direction.NORTH;

            while (true) {
                animation.moveToFast(currentPosition);
                MatrixPosition nextPosition = currentPosition.move(facing);
                if (bruteMatrix.outOfBounds(nextPosition)) {
                    break;
                }
                if (bruteMatrix.get(nextPosition) == '#' && cornersVisited.contains(currentPosition)) {
                    combinations++;
                    break;
                }
                while (bruteMatrix.get(nextPosition) == '#') {
                    cornersVisited.add(currentPosition);
                    facing = facing.rotateRight();
                    nextPosition = currentPosition.move(facing);
                }
                currentPosition = nextPosition;
            }
            animation.repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
        return combinations;
    }

    public List<MatrixPosition> getPath(PuzzleInput input) {
        var matrix = input.toCharMatrix();
        MatrixPosition currentPosition = matrix.find('^').position();
        MatrixPosition start = currentPosition;
        Direction facing = Direction.NORTH;
        BinaryMatrix visited = new BinaryMatrix(matrix);
        while (true) {
            visited.set(currentPosition, true);
            MatrixPosition nextPosition = currentPosition.move(facing);
            if (matrix.outOfBounds(nextPosition)) {
                break;
            }
            while (matrix.get(nextPosition) == '#') {
                facing = facing.rotateRight();
                nextPosition = currentPosition.move(facing);
            }
            currentPosition = nextPosition;
        }
        visited.set(start, false);
        return matrix.filter(visited).map(Matrix.Entry::position).toList();
    }

}
