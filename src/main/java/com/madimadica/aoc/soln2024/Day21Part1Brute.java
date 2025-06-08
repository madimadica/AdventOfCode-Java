package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.annotations.AdventDay;
import org.togetherjava.aoc.core.math.Combinatorics;
import org.togetherjava.aoc.core.math.matrix.Matrix;
import org.togetherjava.aoc.core.math.matrix.MatrixPosition;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.*;
import java.util.stream.Collectors;

// This permutation brute forcing worked for part one... I think it would have taken longer than the universe to complete for part 2, so I had to switch up to memoized stuff
@AdventDay(day=21)
public class Day21Part1Brute implements PuzzleSolution {

    record ShortestKeypadPath(MatrixPosition start, MatrixPosition end, List<String> moves) {}

    record Movement(char start, char stop) {}

    @Override
    public Object part1(PuzzleInput input) {
        var codes = input.getLines();
        var keypadMatrix = PuzzleInput.of("789\n456\n123\n 0A").toCharMatrix();
        var directionMatrix = PuzzleInput.of(" ^A\n<v>").toCharMatrix();

        Map<Character, MatrixPosition> keypadLookup = new HashMap<>();
        for (char c : "0123456789A".toCharArray()) {
            keypadLookup.put(c, keypadMatrix.find(c).position());
        }
        Map<Character, MatrixPosition> dirpadLookup = new HashMap<>();
        for (char c : "<>^vA".toCharArray()) {
            dirpadLookup.put(c, directionMatrix.find(c).position());
        }

        var allKeypadChanges = Combinatorics.arrangeWithRepetition(2, List.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A'));
        var allDirpadChanges = Combinatorics.arrangeWithRepetition(2, List.of('^', '<', '>', 'v', 'A'));

        Map<Movement, List<String>> shortestKeypadPathsMap = getMovementPathsMap(allKeypadChanges, keypadLookup, keypadMatrix);
        Map<Movement, List<String>> shortestDirpadPathsMap = getMovementPathsMap(allDirpadChanges, dirpadLookup, directionMatrix);

        long totalComplexity = 0;
        for (var code : codes) {
            List<String> shortestSequences = new ArrayList<>();
            List<List<String>> keypadMovements = generateMovements(code, shortestKeypadPathsMap);
            List<String> allPerms = generatePermutations(keypadMovements);
            for (String dks1 : allPerms) {
                List<List<String>> foo = generateMovements(dks1, shortestDirpadPathsMap);
                var foo1 = generatePermutations(foo);

                for (String directionalKeypadSequence2 : foo1) {
                    List<List<String>> bar = generateMovements(directionalKeypadSequence2, shortestDirpadPathsMap);
                    var bar1 = generatePermutations(bar);
                    shortestSequences.add(bar1.get(0));
                }
            }
            var overallMins = getMins(shortestSequences);
            String s = code.substring(0, 3);
            while (s.startsWith("0")) {
                s = s.substring(1);
            }
            totalComplexity += (long) Integer.parseInt(s) * overallMins.get(0).length();
        }

        return totalComplexity;
    }

    static List<String> getMins(List<String> list) {
        List<String> output = new ArrayList<>();
        int minSize = Integer.MAX_VALUE;
        for (String s : list) {
            if (s.length() < minSize) {
                minSize = s.length();
                output.clear();
            }
            output.add(s);
        }
        return output;
    }

    static Map<Movement, List<String>> getMovementPathsMap(List<List<Character>> movementPairs, Map<Character, MatrixPosition> keyLookup, Matrix<Character> matrix) {
        Map<Movement, List<String>> out = new HashMap<>();
        for (List<Character> keypadChange : movementPairs) {
            var start = keyLookup.get(keypadChange.get(0));
            var stop = keyLookup.get(keypadChange.get(1));
            String baseMovement = "";
            int right = stop.col() - start.col();
            if (right > 0) {
                baseMovement += ">".repeat(right);
            } else if (right < 0) {
                baseMovement += "<".repeat(-right);
            }
            int down = stop.row() - start.row();
            if (down > 0) {
                baseMovement += "v".repeat(down);
            } else if (down < 0) {
                baseMovement += "^".repeat(-down);
            }

            Set<String> possibleWays = new HashSet<>(baseMovement.isEmpty() ? List.of("") : Combinatorics.getRotations(baseMovement));
            // filter off invalid pos
            if (start.col() == 0 || stop.col() == 0) {
                possibleWays = possibleWays.stream().filter(way ->
                        !matrix.walkUnsafe(start, way).contains(' ')
                ).collect(Collectors.toSet());
            }
            out.put(new Movement(keypadChange.get(0), keypadChange.get(1)), possibleWays.stream().toList());
        }
        return out;
    }

    static List<List<String>> generateMovements(String keys, Map<Movement, List<String>> pathsMap) {
        List<List<String>> out = new ArrayList<>();
        for (int i = 0; i < keys.length(); ++i) {
            char prev = i == 0 ? 'A' : keys.charAt(i - 1);
            char cur = keys.charAt(i);
            List<String> s = pathsMap.get(new Movement(prev, cur));
            out.add(s);
        }
        return out;
    }

    public static List<String> generatePermutations(List<List<String>> stepMoves) {
        List<String> output = new ArrayList<>();
        generatePermutationsHelper(stepMoves, 0, "", output);
        return output;
    }

    private static void generatePermutationsHelper(List<List<String>> stepMoves, int index, String current, List<String> output) {
        // Base case: if we have gone through all the lists, add the current string to results
        if (index == stepMoves.size()) {
            output.add(current);
            return;
        }

        // Recursive case: iterate through the current list and recursively process the next list
        for (String element : stepMoves.get(index)) {
            generatePermutationsHelper(stepMoves, index + 1, current + element + "A", output);
        }
    }

    @Override
    public Object part2(PuzzleInput input) {
        return null;
    }
}
