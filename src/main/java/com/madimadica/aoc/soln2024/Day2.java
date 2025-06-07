package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.ArrayList;
import java.util.List;

public class Day2 implements PuzzleSolution {

    @Override
    public Object part1(PuzzleInput puzzleInput) {
        return puzzleInput.parseNumbers().stream()
                .map(Report::ofLongs)
                .filter(Report::isSafe1)
                .count();
    }

    @Override
    public Object part2(PuzzleInput puzzleInput) {
        return puzzleInput.parseNumbers().stream()
                .map(Report::ofLongs)
                .filter(Report::isSafe2)
                .count();
    }

    enum State {
        UNDEFINED,
        ASC,
        DESC,
        SAME;

        public static boolean invalidDiff(int left, int right) {
            int diff = Math.abs(left - right);
            return diff < 1 || diff > 3;
        }

        public static State of(int prev, int current) {
            if (prev < current) {
                return ASC;
            } else if (current < prev) {
                return DESC;
            } else {
                return SAME;
            }
        }

        public static boolean isValidTransition(State current, int left, int right) {
            if (invalidDiff(left, right)) return false;
            if (current == UNDEFINED) return true;
            if (left < right) {
                return current == ASC;
            }
            return current == DESC;
        }

        public static boolean areValidTransitions(int a, int b, int c) {
            State ab = State.of(a, b);
            if (invalidDiff(a, b)) {
                return false;
            }
            return isValidTransition(ab, b, c);
        }

    }


    record Report(List<Integer> levels) {
        static Report ofLongs(List<Long> longs) {
            return new Report(longs.stream().map(Long::intValue).toList());
        }

        public boolean isSafe1() {
            State state = State.UNDEFINED;
            int prev = levels.get(0);
            for (int i = 1; i < levels.size(); ++i) {
                int current = levels.get(i);
                if (State.invalidDiff(prev, current)) {
                    return false;
                }

                if (state == State.UNDEFINED) {
                    state = State.of(prev, current);
                    if (state == State.SAME) {
                        return false;
                    }
                } else {
                    if (state != State.of(prev, current)) {
                        return false;
                    }
                }
                prev = current;
            }
            return true;
        }

        public boolean isSafe2() {
            State state = State.UNDEFINED;
            int prev = levels.get(0);
            boolean hasPrevFailure = false; // we get exactly one failure attempt/tolerance
            for (int i = 1; i < levels.size(); ++i) {
                int current = levels.get(i);
                boolean invalid = State.invalidDiff(prev, current);

                switch (state) {
                    case ASC -> {
                        if (current <= prev) {
                            invalid = true;
                        }
                    }
                    case DESC -> {
                        if (current >= prev) {
                            invalid = true;
                        }
                    }
                }

                if (invalid) {
                    if (hasPrevFailure) {
                        return false;
                    }
                    hasPrevFailure = true;
                    if (i <= 2) {
                        // Error near the start, given "ABCD", could be either "BCD", "ACD", "ABD" or none
                        int A = levels.get(0);
                        int B = levels.get(1);
                        int C = levels.get(2);
                        int D = levels.get(3);
                        if (State.areValidTransitions(B, C, D)) {
                            state = State.of(B, C);
                        } else if (State.areValidTransitions(A, C, D)) {
                            state = State.of(A, C);
                        } else if (State.areValidTransitions(A, B, D)) {
                            state = State.of(A, B);
                        } else {
                            return false;
                        }
                        prev = D;
                        i = 3;
                    }
                } else {
                    // Define the state to ASC/DESC
                    if (state == State.UNDEFINED) {
                        state = State.of(prev, current);
                    }
                    // Move pointer ahead
                    prev = current;
                }
            }
            return true;
        }

        public boolean isSafe2_bruteForce() { // Used when initially speedrunning
            if (this.isSafe1())
                return true;
            for (int i = 0; i < levels.size(); ++i) {
                Report testReport = this.without(i);
                if (testReport.isSafe1()) {
                    return true;
                }
            }
            return false;
        }

        private Report without(int index) {
            List<Integer> copy = new ArrayList<>(levels);
            copy.remove(index);
            return new Report(copy);
        }
    }

}
