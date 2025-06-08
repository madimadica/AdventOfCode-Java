package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.Regex;
import org.togetherjava.aoc.core.math.matrix.Matrix;
import org.togetherjava.aoc.core.math.matrix.MatrixPosition;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.List;
import java.util.stream.Collectors;

public class Day14 implements PuzzleSolution {

    private static final int MAX_ROWS = 103;
    private static final int MAX_COLS = 101;

    public static class RobotData {
        private MatrixPosition pos;
        int vRows;
        int vCols;

        public RobotData(MatrixPosition pos, int vRows, int vCols) {
            this.pos = pos;
            this.vRows = vRows;
            this.vCols = vCols;
        }

        public static RobotData of(List<Integer> data) {
            int pCol = data.get(0);
            int pRow = data.get(1);
            int vCol = data.get(2);
            int vRow = data.get(3);
            return new RobotData(new MatrixPosition(pRow, pCol), vRow, vCol);
        }

        public void move() {
            var temp = this.pos.move(vRows, vCols);
            var currentRow = temp.row();
            while (currentRow < 0) {
                currentRow = MAX_ROWS + currentRow;
            }
            while (currentRow >= MAX_ROWS) {
                currentRow -= MAX_ROWS;
            }

            var currentCol = temp.col();
            if (currentCol < 0) {
                currentCol = (currentCol % MAX_COLS) + MAX_COLS;
            }
            if (currentCol >= MAX_COLS) {
                currentCol %= MAX_COLS;
            }
            this.pos = new MatrixPosition(currentRow, currentCol);
        }

    }

    @Override
    public Object part1(PuzzleInput input) {
        var robotPositions = input.stream().map(Regex::parseInts).map(RobotData::of).toList();
        for (int i = 0; i < 100; ++i) {
            robotPositions.forEach(RobotData::move);
        }
        var q1Count = 0;
        var q2Count = 0;
        var q3Count = 0;
        var q4Count = 0;
        for (int row = 0; row < 51; row++) {
            for (int col = 0; col < 50; ++col) {
                var targetPos = new MatrixPosition(row, col);
                q1Count += (int) robotPositions.stream().filter(x -> x.pos.equals(targetPos)).count();
            }
        }
        for (int row = 52; row < 103; row++) {
            for (int col = 0; col < 50; ++col) {
                var targetPos = new MatrixPosition(row, col);
                q2Count += (int) robotPositions.stream().filter(x -> x.pos.equals(targetPos)).count();
            }
        }
        for (int row = 0; row < 51; row++) {
            for (int col = 51; col < 101; ++col) {
                var targetPos = new MatrixPosition(row, col);
                q3Count += (int) robotPositions.stream().filter(x -> x.pos.equals(targetPos)).count();
            }
        }
        for (int row = 52; row < 103; row++) {
            for (int col = 51; col < 101; ++col) {
                var targetPos = new MatrixPosition(row, col);
                q4Count += (int) robotPositions.stream().filter(x -> x.pos.equals(targetPos)).count();
            }
        }
        return q1Count * q2Count * q3Count * q4Count;
    }

    @Override
    public Object part2(PuzzleInput input) {
        var robotPositions = input.stream().map(Regex::parseInts).map(RobotData::of).toList();
        int i = 0;
        while (true) {
            robotPositions.forEach(RobotData::move);
            i++;
            var set = robotPositions.stream().map(x -> x.pos).collect(Collectors.toSet());
            if (set.size() == robotPositions.size()) {
                break;
            }
        }
        // Visualize the Easter egg / Christmas tree
//        printChristmasTree(robotPositions);
        return i;
    }

    private void printChristmasTree(List<RobotData> robotPositions) {
        for (int row = 0; row < MAX_ROWS; ++row) {
            for (int col = 0; col < MAX_COLS; ++col) {
                var targetPos = new MatrixPosition(row, col);
                long count = robotPositions.stream().filter(x -> x.pos.equals(targetPos)).count();
                if (count == 0) {
                    System.out.print(" ");
                } else {
                    System.out.print("*");
                }
            }
            System.out.println();
        }
    }

    public Object part2_ifInputWasntNice(PuzzleInput input) {
        var robotPositions = input.stream().map(Regex::parseInts).map(RobotData::of).toList();
        int i = 0;
        while (true) {
            robotPositions.forEach(RobotData::move);
            i++;
            Matrix<Boolean> robots = new Matrix<>(MAX_ROWS, MAX_COLS);
            robotPositions.forEach(robot -> robots.set(robot.pos, true));
            if (robots.getLargestConnectedComponent().size() > 200) {
                break;
            }
        }
        return i;
    }

}
