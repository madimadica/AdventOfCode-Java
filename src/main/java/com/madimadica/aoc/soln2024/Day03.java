package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.Regex;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class Day03 implements PuzzleSolution {

    enum DataType { MUL, DO, DONT }

    record Data(int pos, DataType type, Integer a, Integer b) {}

    @Override
    public Object part1(PuzzleInput puzzleInput) {
        var re = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)");
        int total = 0;
        for (String line : puzzleInput.getLines()) {
            for (var match : re.matcher(line).results().toList()) {
                String lhs = match.group(1);
                String rhs = match.group(2);
                total += Integer.parseInt(lhs) * Integer.parseInt(rhs);
            }
        }
        return total;
    }

    @Override
    public Object part2(PuzzleInput puzzleInput) {
        String allData = String.join("", puzzleInput.getLines());

        List<Data> dataset = new ArrayList<>();

        var reMul = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)");
        var reDo = Pattern.compile("do\\(\\)");
        var reDont = Pattern.compile("don't\\(\\)");

        for (var match : Regex.allMatches(reMul, allData)) {
            String lhs = match.group(1);
            String rhs = match.group(2);
            dataset.add(new Data(match.start(), DataType.MUL, Integer.parseInt(lhs), Integer.parseInt(rhs)));
        }

        for (var match : Regex.allMatches(reDo, allData)) {
            dataset.add(new Data(match.start(), DataType.DO, null, null));
        }

        for (var match : Regex.allMatches(reDont, allData)) {
            dataset.add(new Data(match.start(), DataType.DONT, null, null));
        }

        dataset.sort(Comparator.comparingInt(Data::pos));

        int total = 0;
        boolean enabled = true;
        for (var datapoint : dataset) {
            switch (datapoint.type) {
                case DO -> enabled = true;
                case DONT -> enabled = false;
                case MUL -> {
                    if (enabled) {
                        total += datapoint.a * datapoint.b;
                    }
                }
            }
        }
        return total;
    }
}
