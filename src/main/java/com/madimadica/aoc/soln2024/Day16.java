package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.math.Direction;
import org.togetherjava.aoc.core.math.matrix.Matrix;
import org.togetherjava.aoc.core.math.matrix.MatrixPosition;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.*;

public class Day16 implements PuzzleSolution {
    @Override
    public Object part1(PuzzleInput input) {
        // reindeer start at S (facing East), and at E
        // moving 1 tile = 1 point (not into a wall)
        // rotating L/R = 1000 points
        Matrix<Character> matrix = input.toCharMatrix();
        MatrixPosition start = matrix.find('S').position();
        MatrixPosition end = matrix.find('E').position();

        // Priority queue for A* search
        PriorityQueue<StatePart1> pq = new PriorityQueue<>(Comparator.comparingInt(state -> state.score));
        Set<StatePart1> visited = new HashSet<>();

        // Initialize the search facing East
        pq.add(new StatePart1(start, Direction.EAST, 0));

        // Perform the search
        while (!pq.isEmpty()) {
            StatePart1 current = pq.poll();
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);

            // Check if we found a path to the end
            if (current.position.equals(end)) {
                return current.score;
            }

            for (Direction newDirection : Direction.getCardinal()) {
                int turnCost = (current.direction == newDirection) ? 0 : 1000;
                int newScore = current.score + turnCost;

                MatrixPosition nextPos = current.position.move(newDirection);

                // Check if the move is valid (not a wall and within bounds)
                if (matrix.inBounds(nextPos) && matrix.get(nextPos) != '#') {
                    // Add the forward move cost and enqueue the new state
                    pq.add(new StatePart1(nextPos, newDirection, newScore + 1));
                }
            }
        }

        // If we exhaust the queue without finding a path, return an error
        throw new IllegalStateException("No valid path found!");
    }

    private static class StatePart1 {
        MatrixPosition position;
        Direction direction;
        int score;

        StatePart1(MatrixPosition position, Direction direction, int score) {
            this.position = position;
            this.direction = direction;
            this.score = score;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StatePart1)) return false;
            StatePart1 state = (StatePart1) o;
            return position.equals(state.position) && direction == state.direction;
        }

        @Override
        public int hashCode() {
            return Objects.hash(position, direction);
        }
    }

    @Override
    public Object part2(PuzzleInput input) {
        // Parse input into a matrix
        Matrix<Character> matrix = input.toCharMatrix();
        MatrixPosition start = matrix.find('S').position();
        MatrixPosition end = matrix.find('E').position();

        // Use BFS or similar to find all shortest paths from start to end
        Set<List<MatrixPosition>> shortestPaths = findAllShortestPaths(matrix, start, end);

        // Collect all unique positions from the shortest paths
        Set<MatrixPosition> tilesInShortestPaths = new HashSet<>();
        for (List<MatrixPosition> path : shortestPaths) {
            tilesInShortestPaths.addAll(path);
        }

        // Return the count of unique tiles
        return tilesInShortestPaths.size();
    }

    private Set<List<MatrixPosition>> findAllShortestPaths(
            Matrix<Character> matrix, MatrixPosition start, MatrixPosition end
    ) {
        Queue<StatePart2> queue = new LinkedList<>();
        Map<StateKey, Integer> visited = new HashMap<>();
        Set<List<MatrixPosition>> shortestPaths = new HashSet<>();
        int minScore = Integer.MAX_VALUE;

        // Start BFS with initial state
        queue.add(new StatePart2(start, Direction.EAST, 0, new ArrayList<>(List.of(start))));

        while (!queue.isEmpty()) {
            StatePart2 current = queue.poll();
            MatrixPosition position = current.position;
            int score = current.score;

            // Stop exploring paths that exceed the known minimum score
            if (score > minScore) continue;

            // If we've reached the end, add path to shortestPaths
            if (position.equals(end)) {
                if (score < minScore) {
                    minScore = score;
                    shortestPaths.clear();
                }
                shortestPaths.add(new ArrayList<>(current.path));
                continue;
            }

            // Explore neighbors and rotations
            for (Direction direction : Direction.getCardinal()) {
                MatrixPosition neighbor = position.move(direction);
                if (!matrix.inBounds(neighbor)) {
                    continue;
                }
                char neighborVal = matrix.get(neighbor);
                if (neighborVal == '#') {
                    continue;
                }

                // Calculate the new score
                int moveCost = 1;
                int rotationCost = current.facing == direction ? 0 : 1000;
                int newScore = score + moveCost + rotationCost;

                // Track visited state with position and facing
                StateKey stateKey = new StateKey(neighbor, direction);
                Integer neighborScore = visited.get(stateKey);

                if (neighborScore == null || newScore <= neighborScore) {
                    visited.put(stateKey, newScore);
                    queue.add(new StatePart2(neighbor, direction, newScore, current.pathWith(neighbor)));
                }
            }
        }

        return shortestPaths;
    }

    // State class to track BFS progress
    private static class StatePart2 {
        MatrixPosition position;
        Direction facing;
        int score;
        List<MatrixPosition> path;

        StatePart2(MatrixPosition position, Direction facing, int score, List<MatrixPosition> path) {
            this.position = position;
            this.facing = facing;
            this.score = score;
            this.path = path;
        }

        List<MatrixPosition> pathWith(MatrixPosition next) {
            List<MatrixPosition> newPath = new ArrayList<>(path);
            newPath.add(next);
            return newPath;
        }
    }

    // Helper class to track visited states
    private static class StateKey {
        MatrixPosition position;
        Direction facing;

        StateKey(MatrixPosition position, Direction facing) {
            this.position = position;
            this.facing = facing;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StateKey stateKey = (StateKey) o;
            return position.equals(stateKey.position) && facing == stateKey.facing;
        }

        @Override
        public int hashCode() {
            return Objects.hash(position, facing);
        }
    }
}
