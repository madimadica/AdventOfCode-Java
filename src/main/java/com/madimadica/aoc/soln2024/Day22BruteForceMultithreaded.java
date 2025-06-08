package com.madimadica.aoc.soln2024;

import com.madimadica.utils.Lists;
import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.*;
import java.util.concurrent.*;

/**
 * <p>
 *     <strong>Attention!</strong> Running this will render your computer useless for several minutes!!!<br>
 *     It takes 6 minutes on my 7800X3D 64GB. You have been warned :)
 * </p>
 * <p>
 *     Funny brute force multithreaded implementation to see if its even possible to brute force (it is)
 * </p>
 */
public class Day22BruteForceMultithreaded implements PuzzleSolution {

    static long mixAndPrune(long secret, long value) {
        secret ^= value;
        return secret % 16777216;
    }

    static long nextSecret(long secret) {
        secret = mixAndPrune(secret, secret << 6);
        secret = mixAndPrune(secret, secret >> 5);
        secret = mixAndPrune(secret, secret << 11);
        return secret;
    }

    static class Fast4CircularQueue {
        int[] vals = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
        int insertPos = 0;

        public void add(int n) {
            vals[insertPos++] = n;
            insertPos %= 4;
        }

        public boolean equalsSequence(FastSequence4 list) {
            // insert pos 0: 0 1 2 3
            // insert pos 1: 1 2 3 0
            return list.a == vals[insertPos]
                && list.b == vals[(insertPos + 1) & 3]
                && list.c == vals[(insertPos + 2) & 3]
                && list.d == vals[(insertPos + 3) & 3];
        }
    }

    static class FastSequence4 {
        final int a;
        final int b;
        final int c;
        final int d;

        public FastSequence4(int a, int b, int c, int d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }
    }

    @Override
    public Object part1(PuzzleInput input) {
        long answer = 0;
        for (long secret : input.parseLongPerLine()) {
            for (int i = 0; i < 2000; ++i) {
                secret = nextSecret(secret);
            }
            answer += secret;
        }
        return answer;
    }

    @Override
    public Object part2(PuzzleInput input) {
        List<FastSequence4> allSequences = new ArrayList<>((int) Math.pow(19, 4));
        for (int i = -9; i <= 9; ++i) {
            for (int j = -9; j <= 9; ++j) {
                for (int k = -9; k <= 9; ++k) {
                    for (int l = -9; l <= 9; ++l) {
                        allSequences.add(new FastSequence4(i, j, k, l));
                    }
                }
            }
        }
        List<Long> secrets = input.parseLongPerLine();
        final int THREADS = Math.max(1, Runtime.getRuntime().availableProcessors() - 2);
        System.out.println(THREADS);
        long max = 0;
        try (ExecutorService executor = Executors.newFixedThreadPool(THREADS)) {
            List<Future<Long>> futures = new ArrayList<>();
            for (List<FastSequence4> work : Lists.partitionInto(allSequences, THREADS)) {
                var task = new Worker(secrets, work);
                futures.add(executor.submit(task));
            }
            for (var future : futures) {
                max = Math.max(future.get(), max);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return max;
    }


    private static class Worker implements Callable<Long> {

        List<Long> secrets;
        List<FastSequence4> mySequences;

        public Worker(List<Long> secrets, List<FastSequence4> mySequences) {
            this.secrets = secrets;
            this.mySequences = mySequences;
        }

        /**
         * Given a secret and a sequence, return how much you can sell it for.
         */
        static long evaluate(long secret, FastSequence4 targetSequence) {
            Fast4CircularQueue currentSequence = new Fast4CircularQueue();
            long prevPrice = secret % 10;
            for (int i = 0; i < 2000; ++i) {
                secret = nextSecret(secret);
                long currentPrice = secret % 10;
                long diff = currentPrice - prevPrice;
                currentSequence.add((int) diff);
                if (currentSequence.equalsSequence(targetSequence)) {
                    return currentPrice;
                }
                prevPrice = currentPrice;
            }
            return 0;
        }

        @Override
        public Long call() {
            long best = 0;
            int secretsLength = secrets.size();
            int seqLen = mySequences.size();
            for (int i = 0; i < seqLen; ++i) {
                if (i % 250 == 0) {
                    System.out.println(i + "/" + seqLen);
                }
                var seq = mySequences.get(i);
                // Test every buyer with this sequence to see how much we could sell it for
                long total = 0;
                for (int j = 0; j < secretsLength; ++j) {
                    long r = evaluate(secrets.get(j), seq);
                    total += r;
                }
                best = Math.max(best, total);
            }
            System.out.println("returning " + best);
            return best;
        }
    }


}
