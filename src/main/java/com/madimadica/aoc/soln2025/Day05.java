package com.madimadica.aoc.soln2025;

import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.ArrayList;
import java.util.List;

public class Day05 implements PuzzleSolution {

    record Range(long start, long end) {
        boolean contains(long x) {
            return start <= x && x <= end;
        }
    }


    @Override
    public Object part1(PuzzleInput puzzleInput) {
        var clusters = puzzleInput.getClusters();
        var rawRanges = clusters.get(0);
        var rawIds = clusters.get(1);

        List<Range> ranges = new ArrayList<>();

        for (String rawRange : rawRanges.getLines()) {
            var parts = rawRange.split("-");
            long start = Long.parseLong(parts[0]);
            long end = Long.parseLong(parts[1]);
            ranges.add(new Range(start, end));
        }

        long total = 0;
        for (var rawId : rawIds.getLines()) {
            var id = Long.parseLong(rawId);
            for (var range : ranges) {
                if (range.contains(id)) {
                    total++;
                    break;
                }
            }
        }
        return total;
    }


    class MutRange {
        long start;
        long end;

        public MutRange(long start, long end) {
            this.start = start;
            this.end = end;
        }

        boolean contains(long x) {
            return start <= x && x <= end;
        }

        boolean intersects(MutRange that) {
            // A within B, B within A, A then B, B then A, equal
            return this.start <= that.end && that.start <= this.end;
        }

        @Override
        public String toString() {
            return "[" + String.format("%,d", start) + ", " + String.format("%,d", end) + "]";
        }
    }

    @Override
    public Object part2(PuzzleInput puzzleInput) {
        var clusters = puzzleInput.getClusters();
        var rawRanges = clusters.get(0);

        List<MutRange> ranges = new ArrayList<>();

        for (String rawRange : rawRanges.getLines()) {
            var parts = rawRange.split("-");
            long start = Long.parseLong(parts[0]);
            long end = Long.parseLong(parts[1]);
            ranges.add(new MutRange(start, end));
        }

        while (true) {
            int lenBefore = ranges.size();
            for (int i = 0; i < ranges.size(); ++i) {
                MutRange a = ranges.get(i);
                for (int j = i + 1; j < ranges.size(); ++j) {
                    MutRange b =  ranges.get(j);
                    if (a.intersects(b)) {
                        a.start = Math.min(a.start, b.start);
                        a.end = Math.max(a.end, b.end);
                        ranges.remove(j--);
                    }
                }
            }
            if (lenBefore == ranges.size()) {
                break;
            }
        }

        long total = 0;
        for (var range : ranges) {
            total += range.end - range.start + 1;
        }
        return total;
    }
}
