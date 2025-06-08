package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.Regex;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day05 implements PuzzleSolution {

    record Rule(long before, long after) {}
    record Update(List<Long> nums) {}

    static class Rules implements Comparator<Long> {
        private final List<Rule> rules;

        public Rules(List<Rule> rules) {
            this.rules = rules;
        }

        public int compare(Long a, Long b) {
            for (Rule r : rules) {
                if (r.before == a && r.after == b) {
                    return -1;
                }
                if (r.before == b && r.after == a) {
                    return 1;
                }
            }
            return 0;
        }
    }

    @Override
    public Object part1(PuzzleInput input) {
        var lines = input.getLines();
        List<Rule> ruleList = lines.stream()
                .takeWhile(s -> !s.isEmpty())
                .map(s -> {
                    var nums = Regex.parseLongs(s);
                    return new Rule(nums.get(0), nums.get(1));
                })
                .toList();
        List<Update> updates = lines.stream()
                .skip(ruleList.size() + 1)
                .map(s -> new Update(Regex.parseLongs(s)))
                .toList();

        Rules rules = new Rules(ruleList);

        long result = 0;
        for (Update update : updates) {
            List<Long> given = update.nums();
            List<Long> expected = new ArrayList<>(given);
            expected.sort(rules);
            if (expected.equals(given)) {
                // Find middle number
                long middleNumber = expected.get(expected.size() / 2);
                result += middleNumber;
            }
        }

        return result;
    }

    @Override
    public Object part2(PuzzleInput input) {
        var lines = input.getLines();
        List<Rule> ruleList = lines.stream()
                .takeWhile(s -> !s.isEmpty())
                .map(s -> {
                    var nums = Regex.parseLongs(s);
                    return new Rule(nums.get(0), nums.get(1));
                })
                .toList();
        List<Update> updates = lines.stream()
                .skip(ruleList.size() + 1)
                .map(s -> new Update(Regex.parseLongs(s)))
                .toList();

        Rules rules = new Rules(ruleList);

        long result = 0;
        for (Update update : updates) {
            List<Long> given = update.nums();
            List<Long> expected = new ArrayList<>(given);
            expected.sort(rules);
            if (!expected.equals(given)) {
                // Find middle number
                long middleNumber = expected.get(expected.size() / 2);
                result += middleNumber;
            }
        }

        return result;
    }
}
