package com.madimadica.aoc.soln2025;

import org.togetherjava.aoc.core.Regex;
import org.togetherjava.aoc.core.math.matrix.Matrix;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.ArrayList;
import java.util.List;

public class Day06 implements PuzzleSolution {
    @Override
    public Object part1(PuzzleInput puzzleInput) {
        var lines = puzzleInput.getLines();
//        lines.forEach(System.out::println);
        var temp = puzzleInput.parseNumbers();
        var rows = temp.subList(0, temp.size() - 1);
        var maths = Regex.split(lines.getLast());
        List<List<Long>> columns = new ArrayList<>();
        for (int i = 0; i < maths.size(); ++i) {
            List<Long> list = new ArrayList<>();
            for (var row : rows) {
                list.add(row.get(i));
            }
            columns.add(list);
        }

        return solve(columns, maths);
    }

    private static long solve(List<List<Long>> columns, List<String> maths) {
        long total = 0;
        for (int i = 0; i < maths.size(); ++i) {
            var inputs = columns.get(i);
            var opSign = maths.get(i).charAt(0);
            var isMultiplication = opSign == '*';
            long answer = inputs.getFirst();
            for (int j = 1; j < inputs.size(); ++j) {
                var input = inputs.get(j);
                if (isMultiplication) {
                    answer *= input;
                } else {
                    answer += input;
                }
            }
            total += answer;
        }
        return total;
    }

    @Override
    public Object part2(PuzzleInput puzzleInput) {
        var matrix = puzzleInput.toCharMatrix();
        String lastLine = puzzleInput.getLines().getLast();
        var maths = Regex.split(lastLine);
        List<Long> currentProblem = new ArrayList<>();
        List<List<Long>> problems = new ArrayList<>();
        for (int c = 0; c < matrix.getCols(); ++c) {
            Long value = parseColumn(matrix, c);
            if (value == null) {
                problems.add(currentProblem);
                currentProblem = new ArrayList<>();
            } else {
                currentProblem.add(value);
            }
        }
        problems.add(currentProblem);
        return solve(problems, maths);
    }

    private Long parseColumn(Matrix<Character> matrix, int col) {
        int rows = matrix.getRows();
        long answer = 0;
        boolean foundDigit = false;
        for (int i = 0; i < rows - 1; ++i) {
            char digit = matrix.get(i, col);
            if (digit == ' ') {
                continue;
            }
            if (foundDigit) {
                answer = answer * 10 + (digit - 48);
            } else {
                foundDigit = true;
                answer = digit - 48;
            }
        }
        if (foundDigit) {
            return answer;
        } else {
            return null;
        }
    }
}
