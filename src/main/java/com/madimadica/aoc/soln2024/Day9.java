package com.madimadica.aoc.soln2024;

import org.togetherjava.aoc.core.puzzle.PuzzleInput;
import org.togetherjava.aoc.core.puzzle.PuzzleSolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day9 implements PuzzleSolution {


    @Override
    public Object part1(PuzzleInput input) {
        String line = input.getLines().get(0);
        List<Integer> fileIds = new ArrayList<>();

        for (int i = 0; i < line.length(); ++i) {
            boolean isFile = i % 2 == 0;
            int length = line.charAt(i) - 48;
            if (isFile) {
                int fileId = i / 2;
                for (int j = 0; j < length; ++j) {
                    fileIds.add(fileId);
                }
            } else {
                for (int j = 0; j < length; ++j) {
                    fileIds.add(null);
                }
            }
        }

        int leftPointer = 0;
        int rightPointer = fileIds.size() - 1;
        while (rightPointer > leftPointer - 1) {
            while (fileIds.get(leftPointer) != null) {
                leftPointer++;
            }
            // now leftPoint is null
            while (fileIds.get(rightPointer) == null) {
                rightPointer--;
            }
            // now rightPointer is non null
            // swap
            fileIds.set(leftPointer, fileIds.get(rightPointer));
            fileIds.set(rightPointer, null);
        }
        long checksum = 0;
        // TODO/NOTE: Keep a separate index because I have a bug in the final swap
        int index = 0;
        for (int i = 0; i < fileIds.size(); i++) {
            Integer id = fileIds.get(i);
            if (id != null) {
                checksum += (index * id);
                index++;
            }
        }
        return checksum;
    }

    record Info(boolean isFile, int size, int id) {
        @Override
        public String toString() {
            if (isFile) {
                return "File: size=" + size + " ID=" + id;
            } else {
                return "Free: size=" + size;
            }
        }
    }

    @Override
    public Object part2(PuzzleInput input) {
        String line = input.getLines().get(0);
        List<Info> fileInfo = new ArrayList<>();

        for (int i = 0; i < line.length(); ++i) {
            boolean isFile = i % 2 == 0;
            int length = line.charAt(i) - 48;
            if (isFile) {
                int fileId = i / 2;
                fileInfo.add(new Info(true, length, fileId));
            } else {
                fileInfo.add(new Info(false, length, -1));
            }
        }

        int currentFileId = 0; // value to look for
        Map<Integer, Info> fileLookup = new HashMap<>();
        for (var file : fileInfo.stream().filter(Info::isFile).toList()) {
            fileLookup.put(file.id, file);
            currentFileId = Math.max(file.id, currentFileId);
        }

        while (currentFileId > 0) {
            Info biggestFile = fileLookup.get(currentFileId);
            int originalLoc = fileInfo.indexOf(biggestFile);
            var expectedSpace = biggestFile.size;
            for (int i = 0; i < originalLoc; ++i) {
                var current = fileInfo.get(i);
                if (!current.isFile && current.size >= expectedSpace) {
                    // Found enough free space
                    fileInfo.set(i, biggestFile);
                    fileInfo.set(originalLoc, new Info(false, expectedSpace, -1));
                    // Fragment optional
                    if (current.size > expectedSpace) {
                        // split into new free space
                        fileInfo.add(i + 1, new Info(false, current.size - expectedSpace, -1));
                    }
                    break;
                }
            }
            currentFileId--;

            // Function to defragment free space into a single free space
            for (int i = 0; i < fileInfo.size() - 1; ++i) {
                var current = fileInfo.get(i);
                var next = fileInfo.get(i + 1);
                if (!current.isFile && !next.isFile) {
                    // Merge 2 free spaces into 1
                    int totalSpace = current.size + next.size;
                    fileInfo.set(i, new Info(false, totalSpace, -1));
                    fileInfo.remove(i + 1);
                    i--;
                }
            }
        }

        long checksum = 0;
        int index = 0;
        for (Info info : fileInfo) {
            if (info.isFile) {
                for (int j = 0; j < info.size; ++j) {
                    checksum += ((long) index * info.id);
                    index++;
                }
            }
            else {
                index += info.size;
            }
        }
        return checksum;
    }

}
