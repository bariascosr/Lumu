package com.lumu.puzzle_decoder.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.lumu.puzzle_decoder.client.PuzzleServerClient;
import com.lumu.puzzle_decoder.exception.PuzzleNotCompleteException;
import com.lumu.puzzle_decoder.model.PuzzleFragment;

import reactor.core.publisher.Mono;

public class PuzzleService {

    private static final int INITIAL_BATCH_SIZE = 100;
    private static final int MAX_IDS = 10_000;

    private final PuzzleServerClient puzzleServerClient;

    public PuzzleService(PuzzleServerClient puzzleServerClient) {
        this.puzzleServerClient = puzzleServerClient;
    }

    public Mono<String> solve() {
        
        return discoverFragments(
                1,
                INITIAL_BATCH_SIZE,
                new HashMap<>())
                .map(this::assemble);
    }

    private Mono<Map<Integer, PuzzleFragment>> discoverFragments(
            int firstId,
            int batchSize,
            Map<Integer, PuzzleFragment> discovered) {

        int lastId = Math.min(
                firstId + batchSize - 1,
                MAX_IDS);

        List<Integer> ids = generateIds(firstId, lastId);

        return puzzleServerClient.fetchFragments(ids)
                .collectList()
                .flatMap(fragments -> {

                    addFragments(discovered, fragments);

                    if (isComplete(fragments, discovered)) {
                        return Mono.just(discovered);
                    }

                    if (lastId >= MAX_IDS) {
                        return Mono.error(
                                new PuzzleNotCompleteException(
                                        "Puzzle could not be completed "
                                                + "within "
                                                + MAX_IDS
                                                + " requests."));
                    }

                    int nextBatchSize = Math.min(
                            batchSize * 2,
                            MAX_IDS - lastId);

                    return discoverFragments(
                            lastId + 1,
                            nextBatchSize,
                            discovered);
                });
    }

    private void addFragments(
            Map<Integer, PuzzleFragment> discovered,
            List<PuzzleFragment> fragments) {

        fragments.forEach(fragment ->
                discovered.putIfAbsent(
                        fragment.index(),
                        fragment));
    }

    private boolean isComplete(List<PuzzleFragment> currentBatch, Map<Integer, PuzzleFragment> discovered) {

        if (discovered.isEmpty()) {
            return false;
        }

        int highestIndex = discovered.keySet()
                .stream()
                .max(Integer::compareTo)
                .orElse(-1);

        boolean contiguousIndexes =
                discovered.size() == highestIndex + 1;

        if (!contiguousIndexes) {
            return false;
        }

        Map<Integer, Long> occurrences =
                currentBatch.stream()
                        .collect(Collectors.groupingBy(
                                PuzzleFragment::index,
                                Collectors.counting()));

        return IntStream.rangeClosed(0, highestIndex)
                .allMatch(index ->
                        occurrences.getOrDefault(index, 0L) >= 2);
    }


    private String assemble(
            Map<Integer, PuzzleFragment> fragments) {

        return fragments.values()
                .stream()
                .sorted(Comparator.comparingInt(
                        PuzzleFragment::index))
                .map(PuzzleFragment::text)
                .collect(Collectors.joining(" "));
    }

    private List<Integer> generateIds(
            int firstId,
            int lastId) {

        return IntStream.rangeClosed(firstId, lastId)
                .boxed()
                .toList();
    }
}
