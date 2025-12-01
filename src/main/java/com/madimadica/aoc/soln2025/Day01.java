package com.madimadica.aoc.soln2025;

import org.togetherjava.aoc.core.annotations.AdventDay;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;
import org.togetherjava.aoc.core.utils.Counter;

import java.util.List;

@AdventDay(day = 1)
public class Day01 implements PuzzleSolution {

	@Override
	public Object part1(PuzzleInput input) {
		long current = 50;
		long total = 0;
		for (String s : input.getLines()) {
			char c = s.charAt(0);
			int x = Integer.parseInt(s.substring(1));
			if (c == 'L') {
				x = -x;
			}

			current += x;
			current %= 100;
			if (current < 0) {
				current += 100;
			}
			if (current == 0) {
				total++;
			}
		}

		return total;
	}

	@Override
	public Object part2(PuzzleInput input) {
		long current = 50;
		long total = 0;
		for (String s : input.getLines()) {
			char c = s.charAt(0);
			int x = Integer.parseInt(s.substring(1));
			boolean isLeft = c == 'L';
			for (int i = 0; i < x; ++i) {
				current += (isLeft ? -1 : 1);
				current %= 100;
				if (current < 0) {
					current += 100;
				}
				if (current == 0) {
					total++;
				}
			}
		}

		return total;
	}
}