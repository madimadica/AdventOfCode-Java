package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.math.Direction;
import org.togetherjava.aoc.core.math.matrix.Matrix;
import org.togetherjava.aoc.core.math.matrix.MatrixPosition;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.*;

public class Day18 implements PuzzleSolution {
    @Override
    public Object part1(PuzzleInput input) {
        return solve(input, 1024);
    }

    public int solve(PuzzleInput input, int length) {
        var corruptedPositions = input.parseNumbers().stream().map(li -> new MatrixPosition(li.get(1).intValue(), li.get(0).intValue())).toList();
        Matrix<Character> grid = new Matrix<>(71, 71);
        grid.setAll(' ');
        MatrixPosition start = new MatrixPosition(0, 0);
        for (int i = 0; i < length; ++i) {
            grid.set(corruptedPositions.get(i), '#');
        }

        MatrixPosition destination = new MatrixPosition(70, 70); // Zero-based indices, bottom right corner


        // BFS to find the shortest path
        Queue<BFSState> queue = new ArrayDeque<>();
        Set<MatrixPosition> visited = new HashSet<>();
        queue.offer(new BFSState(start, 1)); // Start with distance 1
        visited.add(start);


        while (!queue.isEmpty()) {
            BFSState currentState = queue.poll();
            MatrixPosition currentPosition = currentState.position;

            // Check if we reached the destination
            if (currentPosition.equals(destination)) {
                // Total steps between tiles, not total visited
                return currentState.distance - 1;
            }

            // Explore neighbors
            for (var dir : Direction.getCardinal()) {
                MatrixPosition neighbor = currentPosition.move(dir);

                // Check bounds, walkability, and if not already visited
                if (isValid(neighbor, grid, visited)) {
                    visited.add(neighbor);
                    queue.offer(new BFSState(neighbor, currentState.distance + 1));
                }
            }
        }

        throw new RuntimeException("no soln");
    }


    // Helper to validate a position
    private boolean isValid(MatrixPosition pos, Matrix<Character> grid, Set<MatrixPosition> visited) {
        return grid.inBounds(pos) && grid.get(pos) != '#' && !visited.contains(pos);
    }

    // BFS state class
    private static class BFSState {
        MatrixPosition position;
        int distance;

        BFSState(MatrixPosition position, int distance) {
            this.position = position;
            this.distance = distance;
        }
    }

    @Override
    public Object part2(PuzzleInput input) {
        // Could binary search, but speedrunning lol
        for (int i = 1024; ; ++i) {
            try {
                solve(input, i);
            } catch (RuntimeException e) {
                return input.getLines().get(i - 1);
            }
        }
    }
}
