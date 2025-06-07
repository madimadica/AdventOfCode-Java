package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.annotations.AdventDay;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;
import org.togetherjava.aoc.core.utils.Counter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AdventDay(day = 1)
public class Day1 implements PuzzleSolution {

	@Override
	public Object part1(PuzzleInput input) {
		var cols = input.getColumnsAsLongs();
		List<Long> lhs = cols.get(0).stream().sorted().toList();
		List<Long> rhs = cols.get(1).stream().sorted().toList();

		final int len = lhs.size();
		long totalDiff = 0;
		for (int i = 0; i < len; ++i) {
			totalDiff += Math.abs(lhs.get(i) - rhs.get(i));
		}

		return totalDiff;
	}

	@Override
	public Object part2(PuzzleInput input) {
		var cols = input.getColumnsAsLongs();
		List<Long> lhs = cols.get(0);
		List<Long> rhs = cols.get(1);
		Counter<Long> rhsCounts = new Counter<>(rhs);

		return lhs.stream()
				.map(x -> x * rhsCounts.getCount(x))
				.reduce(0L, Long::sum);
	}
}
