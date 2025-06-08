package com.madimadica.aoc.soln2024;

import com.madimadica.utils.NumberUtils;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.List;

public class Day7Bitmasks implements PuzzleSolution {

    @Override
    public Object part1(PuzzleInput input) {
        long answer = 0;
        for (List<Long> line : input.parseNumbers()) {
            final long expected = line.get(0);
            List<Long> operands = line.subList(1, line.size());
            int opLen = operands.size() - 1;
            final int LUB = 1 << opLen;
            for (int bitmask = 0; bitmask < LUB; ++bitmask) {
                long prev = operands.get(0);
                for (int j = 0; j < opLen; ++j) {
                    int operatorMask = (bitmask >>> j) & 1;
                    long currentOperand = operands.get(j + 1);
                    switch (operatorMask) {
                        case 0 -> prev += currentOperand;
                        case 1 -> prev *= currentOperand;
                    }
                }
                if (prev == expected) {
                    answer += expected;
                    break;
                }
            }
        }
        return answer;
    }

    @Override
    public Object part2(PuzzleInput input) {
        final int BASE = 3;
        long answer = 0;
        long[] powers = new long[38];
        powers[0] = 1;
        for (int i = 1; i < 38; ++i) {
            powers[i] = BASE * powers[i - 1];
        }
        for (List<Long> line : input.parseNumbers()) {
            final long expected = line.get(0);
            List<Long> operands = line.subList(1, line.size());
            int opLen = operands.size() - 1;
            final long LUB = powers[opLen];
            for (long bitmask = 0; bitmask < LUB; ++bitmask) {
                long prev = operands.get(0);
                for (int j = 0; j < opLen; ++j) {
                    int operatorMask = (int) ((bitmask / powers[j]) % BASE);
                    long currentOperand = operands.get(j + 1);
                    switch (operatorMask) {
                        case 0 -> prev += currentOperand;
                        case 1 -> prev *= currentOperand;
                        case 2 -> prev = NumberUtils.concat(prev, currentOperand);
                    }
                }
                if (prev == expected) {
                    answer += expected;
                    break;
                }
            }
        }
        return answer;
    }

}
