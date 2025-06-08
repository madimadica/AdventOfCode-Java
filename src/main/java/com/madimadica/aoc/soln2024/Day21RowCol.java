package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.*;

// Day 21 with 2-dimensional indices
public class Day21RowCol implements PuzzleSolution {
    @Override
    public Object part1(PuzzleInput input) {
        return solve(input, 4);
    }

    @Override
    public Object part2(PuzzleInput input) {
        return solve(input, 27);
    }

    private static final String NUMERICAL_KEYPAD = "789456123 0A";
    private static final String DIRECTIONAL_KEYPAD = " ^A<v>";

    private record Args(int start, int end, int robots, int blankRow, int blankCol) {}
    private record Visit(int row, int col, String presses) {}

    private static final Map<Args, Long> CACHE = new HashMap<>();

    private static Long solve(PuzzleInput input, int robots) {
        List<String> codes = input.getLines();
        long totalComplexity = 0;
        for (String code : codes) {
            int curRow = 3;
            int curCol = 2;
            long result = 0;
            for (char ch : code.toCharArray()) {
                for (int row = 0; row < 4; ++row) {
                    for (int col = 0; col < 3; ++col) {
                        if (NUMERICAL_KEYPAD.charAt(row * 3 + col) == ch) {
                            result += cheapestDirPad(curRow, curCol, row, col, robots, 3, 0);
                            curRow = row;
                            curCol = col;
                        }
                    }
                }
            }

            String codeNum = code.substring(0, 3);
            while (codeNum.startsWith("0")) {
                codeNum = codeNum.substring(1);
            }
            totalComplexity += result * Integer.parseInt(codeNum);

        }

        return totalComplexity;
    }

    private static long cheapestDirPad(int startRow, int startCol, int endRow, int endCol, int robots, int blankRow, int blankCol) {
        Args args = new Args(startRow * 3 + startCol, endRow * 3 + endCol, robots, blankRow, blankCol);
        Long answer = CACHE.get(args);
        if (answer != null) {
            return answer;
        }

        answer = Long.MAX_VALUE;
        Deque<Visit> q = new LinkedList<>();
        q.push(new Visit(startRow, startCol, ""));

        while (!q.isEmpty()) {
            Visit v = q.pollFirst();
            if (v.row == endRow && v.col == endCol) {
                // When a permutation reaches the end, check how expensive it is to run that
                // Find the min of all these permutations
                long childAnswer = cheapestRobot(v.presses + "A", robots - 1);
                answer = Math.min(answer, childAnswer);
                continue;
            }
            if (v.row == blankRow && v.col == blankCol) {
                continue; // OOB keypad
            } else {
                // This is where 'permutations' are generated
                if (v.row < endRow) {
                    q.addLast(new Visit(v.row + 1, v.col, v.presses + "v"));
                } else if (v.row > endRow) {
                    q.addLast(new Visit(v.row - 1, v.col, v.presses + "^"));
                }
                if (v.col < endCol) {
                    q.addLast(new Visit(v.row, v.col + 1, v.presses + ">"));
                } else if (v.col > endCol) {
                    q.addLast(new Visit(v.row, v.col - 1, v.presses + "<"));
                }
            }
        }
        CACHE.put(args, answer);
        return answer;
    }

    private static long cheapestRobot(String presses, int robots) {
        if (robots == 1) {
            return presses.length();
        }
        long result = 0;

        int curRow = 0;
        int curCol = 2;

        for (char ch : presses.toCharArray()) {
            for (int row = 0; row < 2; ++row) {
                for (int col = 0; col < 3; ++col) {
                    if (DIRECTIONAL_KEYPAD.charAt(row * 3 + col) == ch) {
                        result += cheapestDirPad(curRow, curCol, row, col, robots, 0, 0);
                        curRow = row;
                        curCol = col;
                    }
                }
            }
        }
        return result;
    }

}
