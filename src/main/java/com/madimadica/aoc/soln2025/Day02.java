package com.madimadica.aoc.soln2025;

import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

public class Day02 implements PuzzleSolution {
    @Override
    public Object part1(PuzzleInput puzzleInput) {
        var ranges = puzzleInput.splitLines(",").get(0);
        long total = 0;
        for (var range : ranges) {
            var parts = range.split("-");
            var start = Long.parseLong(parts[0]);
            var end = Long.parseLong(parts[1]);
            for (long x = start; x <= end; ++x) {
                if (isInvalid(String.valueOf(x))) {
                    total += x;
                }
            }
        }
        return total;
    }

    static boolean isInvalid(String s) {
        int len = s.length();
        if ((len & 1) == 1) {
            return false;
        }
        int middle = len >> 1;
        for (int i = 0; i < middle; ++i) {
            char lhs = s.charAt(i);
            char rhs = s.charAt(i + middle);
            if (lhs != rhs) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object part2(PuzzleInput puzzleInput) {
        var ranges = puzzleInput.splitLines(",").get(0);
        long total = 0;
        for (var range : ranges) {
            var parts = range.split("-");
            var start = Long.parseLong(parts[0]);
            var end = Long.parseLong(parts[1]);
            for (long x = start; x <= end; ++x) {
                if (isInvalid2(String.valueOf(x))) {
                    total += x;
                }
            }
        }
        return total;
    }

    static boolean isInvalid2(String s) {
        for (int i = 2; i <= s.length(); ++i) {
            if (isInvalid2(s, i)) {
                return true;
            }
        }
        return false;
    }

    static boolean isInvalid2(String s, int parts) {
        int len = s.length();
        if (len % parts != 0) {
            return false;
        }
        int sectionLength = len / parts;
        char[] pairs = new char[parts];
        for (int i = 0; i < sectionLength; ++i) {
            /* check each position of the pairs in unison
            with parts = 3:

            123412341234
            ^   ^   ^
             ^   ^   ^
              ^   ^   ^
               ^   ^   ^
             */
            for (int j = 0; j < parts; ++j) { // check each pair at that position
                int offset = j * sectionLength + i;
                pairs[j] = s.charAt(offset);
            }
            char target = pairs[0];
            for (int j = 1; j < parts; ++j) {
                if (pairs[j] != target) {
                    return false;
                }
            }
        }
        return true;
    }

}
