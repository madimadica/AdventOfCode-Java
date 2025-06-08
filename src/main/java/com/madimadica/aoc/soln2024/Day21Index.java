package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.*;

// Day 21 with 1-dimensional indices
public class Day21Index implements PuzzleSolution {
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
    private static final Map<Character, Integer> NUMERICAL_INDEX_MAP = new HashMap<>();
    private static final Map<Character, Integer> DIRECTIONAL_INDEX_MAP = new HashMap<>();
    static {
        for (int i = 0; i < NUMERICAL_KEYPAD.length(); ++i) {
            NUMERICAL_INDEX_MAP.put(NUMERICAL_KEYPAD.charAt(i), i);
        }
        for (int i = 0; i < DIRECTIONAL_KEYPAD.length(); ++i) {
            DIRECTIONAL_INDEX_MAP.put(DIRECTIONAL_KEYPAD.charAt(i), i);
        }
    }

    private record Args(int start, int end, int robots, int deadzone) {}

    private static final Map<Args, Long> CACHE = new HashMap<>();

    private static Long solve(PuzzleInput input, int robots) {
        List<String> codes = input.getLines();
        long totalComplexity = 0;
        for (String code : codes) {
            int currentIndex = 11;
            long result = 0;
            for (char ch : code.toCharArray()) {
                int nextIndex = NUMERICAL_INDEX_MAP.get(ch);
                result += cheapestMove(currentIndex, nextIndex, robots, 9);
                currentIndex = nextIndex;
            }
            String codeNum = code.substring(0, 3);
            while (codeNum.startsWith("0")) {
                codeNum = codeNum.substring(1);
            }
            totalComplexity += result * Integer.parseInt(codeNum);

        }

        return totalComplexity;
    }

    private static long cheapestMove(int start, int end, int robots, int deadzone) {
        Args args = new Args(start, end, robots, deadzone);
        Long answer = CACHE.get(args);
        if (answer != null) {
            return answer;
        }
        record Visit(int index, String presses) {}

        answer = Long.MAX_VALUE;
        Deque<Visit> q = new LinkedList<>();
        q.push(new Visit(start, ""));

        while (!q.isEmpty()) {
            Visit v = q.pollFirst();
            if (v.index == end) {
                // When a permutation reaches the end, check how expensive it is to run that
                // Find the min of all these permutations
                answer = Math.min(answer, cheapestSequence(v.presses + "A", robots - 1));
                continue;
            }
            if (v.index == deadzone) {
                continue; // Out of bounds, dropped from queue
            } else {
                // This is where movement permutations are generated
                // Add both the vertical and horizontal options
                int crow = v.index / 3;
                int ccol = v.index % 3;
                int erow = end / 3;
                int ecol = end % 3;
                if (crow < erow) {
                    q.addLast(new Visit(v.index + 3, v.presses + "v"));
                } else if (crow > erow) {
                    q.addLast(new Visit(v.index - 3, v.presses + "^"));
                }
                if (ccol < ecol) {
                    q.addLast(new Visit(v.index + 1, v.presses + ">"));
                } else if (ccol > ecol) {
                    q.addLast(new Visit(v.index - 1 , v.presses + "<"));
                }
            }
        }
        CACHE.put(args, answer);
        return answer;
    }

    private static long cheapestSequence(String presses, int robots) {
        if (robots == 1) {
            return presses.length();
        }
        long result = 0;
        int currentIndex = 2;
        for (char ch : presses.toCharArray()) {
            int nextIndex = DIRECTIONAL_INDEX_MAP.get(ch);
            result += cheapestMove(currentIndex, nextIndex, robots, 0);
            currentIndex = nextIndex;
        }
        return result;
    }

}
