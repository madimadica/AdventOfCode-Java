package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;
import org.togetherjava.aoc.core.utils.Tuple;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Painful implementation of electrical circuitry that I originally solved on paper, and then translated into code.
 * This code "works" for my input, but I'm not 100% confident that there are no edge cases depending on where your input wires crossed
 */
public class Day24 implements PuzzleSolution {

    enum State {
        ON, OFF, UNDEFINED;

        public static State ofBit(String bit) {
            return bit.equals("1") ? ON : OFF;
        }

        public static State ofBool(Boolean b) {
            if (b == null) return UNDEFINED;
            return b ? ON : OFF;
        }
    }

    enum GateType {
        AND, OR, XOR;

        public static GateType of(String name) {
            return switch(name) {
                case "XOR" -> XOR;
                case "AND" -> AND;
                case "OR" -> OR;
                default -> throw new IllegalArgumentException();
            };
        }

        public State apply(State left, State right) {
            return switch (this) {
                case AND -> State.ofBool(left == State.ON && right == State.ON);
                case OR -> State.ofBool(left == State.ON || right == State.ON);
                case XOR -> State.ofBool((left == State.ON) ^ (right == State.ON));
            };
        }
    }

    static class Gate {
        String left, right, out;
        GateType type;

        public Gate(String left, String right, String out, GateType type) {
            this.left = left;
            this.right = right;
            this.out = out;
            this.type = type;
        }

        public boolean hasInputs(String a, String b) {
            return (left.equals(a) && right.equals(b)) || (left.equals(b) && right.equals(a));
        }

        public boolean hasInput(String a) {
            return (left.equals(a) || right.equals(a));
        }

        @Override
        public String toString() {
            return "Gate[%s %s %s] => %s".formatted(left, type, right, out);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Gate gate = (Gate) o;
            return Objects.equals(left, gate.left) && Objects.equals(right, gate.right) && Objects.equals(out, gate.out) && type == gate.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right, out, type);
        }
    }

    static long eval(List<Gate> gates, Map<String, State> wireMap) {
        wireMap = new HashMap<>(wireMap);

        List<Gate> remainingGates = new ArrayList<>(gates);
        while (wireMap.containsValue(State.UNDEFINED)) {
            for (int i = remainingGates.size(); i --> 0;) {
                Gate gate = remainingGates.get(i);
                State left = wireMap.get(gate.left);
                State right = wireMap.get(gate.right);
                if (left == State.UNDEFINED || right == State.UNDEFINED) {
                    continue;
                }
                State output = gate.type.apply(left, right);
                wireMap.put(gate.out, output);
                remainingGates.remove(i);
            }
        }
        long result = 0;
        for (var entry : wireMap.entrySet()) {
            String name = entry.getKey();
            if (!name.startsWith("z")) {
                continue;
            }
            if (entry.getValue() == State.OFF) {
                continue;
            }
            result |= (1L << Integer.parseInt(name.substring(1)));
        }
        return result;
    }

    static int firstDiff(long expected, long actual) {
        for (int i = 0; i < 64; ++i) {
            long mask = 1L << i;
            if ((expected & mask) != (actual & mask)) {
                return i;
            }
        }
        return -1;
    }

    static Tuple<Map<String, State>, List<Gate>> parseInput(PuzzleInput input) {
        Map<String, State> wireMap = new HashMap<>();
        var clusters = input.getClusters();
        for (var parts : clusters.get(0).splitLines(": ")) {
            wireMap.put(parts.get(0), State.ofBit(parts.get(1)));
        }

        List<Gate> gates = new ArrayList<>();

        for (var gateData : clusters.get(1).splitLines()) {
            String left = gateData.get(0);
            String op = gateData.get(1);
            String right = gateData.get(2);
            String out = gateData.get(4);
            for (String wireName : List.of(left, right, out)) {
                if (!wireMap.containsKey(wireName)) {
                    wireMap.put(wireName, State.UNDEFINED);
                }
            }
            gates.add(new Gate(left, right, out, GateType.of(op)));
        }
        return new Tuple<>(wireMap, gates);
    }

    @Override
    public Object part1(PuzzleInput input) {
        var inputData = parseInput(input);
        return eval(inputData.getB(), inputData.getA());
    }

    @Override
    public Object part2(PuzzleInput input) {
        var inputData = parseInput(input);
        Map<String, State> wireMap = inputData.getA();
        List<Gate> gates = inputData.getB();

        final long expected = readInput(wireMap, "x") + readInput(wireMap, "y");

        List<String> errors = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            long currentOutput = eval(gates, wireMap);
            int badIndex = firstDiff(expected, currentOutput);
            errors.addAll(fixGates(gates, badIndex));
        }

        return errors.stream().sorted().collect(Collectors.joining(","));
    }

    static List<String> fixGates(List<Gate> gates, int bit) {
        String xi = "x%02d".formatted(bit);
        String yi = "y%02d".formatted(bit);
        String zi = "z%02d".formatted(bit);

        final String carryIn;
        {
            String xPrev = "x%02d".formatted(bit - 1);
            String yPrev = "y%02d".formatted(bit - 1);
            Gate prevAnd = gates.stream().filter(gate -> gate.hasInputs(xPrev, yPrev) && gate.type == GateType.AND).findFirst().get();
            Gate prevCarryOut = gates.stream().filter(gate -> gate.hasInput(prevAnd.out)).findFirst().get();
            carryIn = prevCarryOut.out;
        }

        List<Gate> roots = gates.stream().filter(gate -> gate.hasInputs(xi, yi)).toList();
        Gate rootXor = roots.stream().filter(g -> g.type == GateType.XOR).findFirst().get();
        Gate rootAnd = roots.stream().filter(g -> g.type == GateType.AND).findFirst().get();

        List<Gate> usesCarry = gates.stream().filter(gate -> gate.hasInput(carryIn)).toList();
        Gate carryXor = usesCarry.stream().filter(g -> g.type == GateType.XOR).findFirst().get();
        Gate carryAnd = usesCarry.stream().filter(g -> g.type == GateType.AND).findFirst().get();
        Gate carryOut = gates.stream().filter(gate -> (gate.hasInput(carryAnd.out) || gate.hasInput(rootAnd.out)) && gate.type == GateType.OR).findFirst().get();

        if (!carryXor.out.equals(zi)) {
            // Find zi and swap
            for (Gate other : List.of(rootXor, rootAnd, carryAnd, carryOut)) {
                if (other.out.equals(zi)) {
                    swapOutputs(other, carryXor);
                    return List.of(other.out, carryXor.out);
                }
            }
        } else {
            // Harder
            Set<Gate> errors = new HashSet<>();
            if (!carryOut.hasInput(rootAnd.out)) {
                errors.add(rootAnd);
            }
            if (!carryOut.hasInput(carryAnd.out)) {
                errors.add(carryAnd);
            }
            if (!carryAnd.hasInput(rootXor.out)) {
                errors.add(rootXor);
            }
            if (!carryXor.hasInput(rootXor.out)) {
                errors.add(rootXor);
            }
            if (errors.size() != 2) {
                throw new IllegalStateException();
            }
            List<Gate> errorGates = errors.stream().toList();
            swapOutputs(errorGates.get(0), errorGates.get(1));
            return List.of(errorGates.get(0).out, errorGates.get(1).out);
        }
        throw new IllegalStateException();
    }

    static void swapOutputs(Gate a, Gate b) {
        String temp = a.out;
        a.out = b.out;
        b.out = temp;
    }

    static long readInput(Map<String, State> wireMap, String prefix) {
        long result = 0;
        for (var entry : wireMap.entrySet()) {
            String wireName = entry.getKey();
            if (!wireName.startsWith(prefix)) {
                continue;
            }
            if (entry.getValue() != State.ON) {
                continue;
            }
            result |= (1L << Integer.parseInt(wireName.substring(1)));
        }
        return result;
    }


}
