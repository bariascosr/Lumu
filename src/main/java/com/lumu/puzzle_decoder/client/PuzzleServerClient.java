package com.lumu.puzzle_decoder.client;

import java.util.List;

import com.lumu.puzzle_decoder.model.PuzzleFragment;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PuzzleServerClient {
    
    Mono<PuzzleFragment> fetchFragment(int id);

    Flux<PuzzleFragment> fetchFragments(List<Integer> ids);
}
