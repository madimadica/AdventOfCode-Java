package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.math.Combinatorics;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;
import org.togetherjava.aoc.core.utils.Triple;
import org.togetherjava.aoc.core.utils.Tuple;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Graph {
    Set<String> nodes = new HashSet<>();
    Set<Tuple<String, String>> edges = new HashSet<>();

    public void addEdge(String a, String b) {
        nodes.add(a);
        nodes.add(b);
        edges.add(new Tuple<>(a, b));
    }

    public Set<String> getAdjacentNodes(String source) {
        Set<String> out = getAdjacentSubgraph(source);
        out.remove(source);
        return out;
    }

    public Set<String> getAdjacentSubgraph(String source) {
        Set<String> out = new HashSet<>();
        for (var edge : edges) {
            if (edge.getA().equals(source) || edge.getB().equals(source)) {
                out.add(edge.getA());
                out.add(edge.getB());
            }
        }
        return out;
    }

    public boolean hasEdge(String a, String b) {
        for (var edge : edges) {
            var ea = edge.getA();
            var eb = edge.getB();
            if ((ea.equals(a) && eb.equals(b)) || (eb.equals(a) && ea.equals(b))) {
                return true;
            }
        }
        return false;
    }
}


class Graph2 {
    Map<String, Set<String>> nodes = new HashMap<>();

    public void addEdge(String a, String b) {
        nodes.computeIfAbsent(a, ignored -> new HashSet<>()).add(b);
        nodes.computeIfAbsent(b, ignored -> new HashSet<>()).add(a);
    }

    public Set<String> getAdjacentNodes(String source) {
        return Set.copyOf(nodes.get(source));
    }

    public Set<String> getAdjacentSubgraph(String source) {
        var set = new HashSet<>(nodes.get(source));
        set.add(source);
        return set;
    }

    public boolean hasEdge(String a, String b) {
        Set<String> source = nodes.get(a);
        if (source == null) {
            return false;
        }
        return source.contains(b);
    }
}



public class Day23 implements PuzzleSolution {
    @Override
    public Object part1(PuzzleInput input) {
        Graph g = new Graph();
        for (List<String> x : input.splitLines("-")) {
            g.addEdge(x.get(0), x.get(1));
        }
        Set<String> seen = new HashSet<>();
        Set<Triple<String, String, String>> trios = new HashSet<>();
        for (String node : g.nodes) {
            if (!node.startsWith("t")) {
                continue;
            }
            if (!seen.add(node)) {
                continue;
            }
            var adjNodes = g.getAdjacentNodes(node).stream().toList();
            Set<Tuple<String, String>> otherPairs = new HashSet<>();
            for (int i = 0; i < adjNodes.size(); ++i) {
                for (int j = i + 1; j < adjNodes.size(); ++j) {
                    if (g.hasEdge(adjNodes.get(i), adjNodes.get(j))) {
                        otherPairs.add(new Tuple<>(adjNodes.get(i), adjNodes.get(j)));
                    }
                }
            }
            for (var pair : otherPairs) {
                var ordered = Stream.of(node, pair.getA(), pair.getB()).sorted().toList();
                trios.add(new Triple<>(ordered.get(0), ordered.get(1), ordered.get(2)));
            }
        }
//        System.out.println(trios);
        return trios.size();
    }

    public static <T> Set<T> intersection(Set<T> setA, Set<T> setB) {
        Set<T> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);
        return intersection;
    }

    @Override
    public Object part2(PuzzleInput input) {
//        System.out.println(input.getLines().size());
        Graph2 g = new Graph2();
        for (List<String> x : input.splitLines("-")) {
            g.addEdge(x.get(0), x.get(1));
        }
//        System.out.println(g.nodes.size());
        Set<String> largestSubgraph = new HashSet<>();
        for (var entry : g.nodes.entrySet()) {
            String source = entry.getKey();
            List<String> adjNodes = g.getAdjacentNodes(entry.getKey()).stream().toList();
            for (int i = 11; i < 13; ++i) {
                var combinations = Combinatorics.choose(adjNodes, i);
                for (var combo : combinations) {
                    var subgraph = this.tryConnectGraph(g, source, new HashSet<>(combo));
                    if (subgraph.size() > largestSubgraph.size()) {
                        largestSubgraph = subgraph;
                    }
                }
            }
        }
        return largestSubgraph.stream().sorted().collect(Collectors.joining(","));
    }

    private Set<String> tryConnectGraph(Graph2 g, String source, Set<String> adj) {
        Set<String> intersection = new HashSet<>(adj);
        intersection.add(source);
        for (String adjNode : adj) {
            var adjNodes2 = g.getAdjacentSubgraph(adjNode);
            intersection = intersection(intersection, adjNodes2);
        }
        return intersection;
    }
}
