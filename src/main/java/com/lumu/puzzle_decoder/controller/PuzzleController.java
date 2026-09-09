package com.lumu.puzzle_decoder.controller;

import com.lumu.puzzle_decoder.service.PuzzleService;
import reactor.core.publisher.Mono;

public class PuzzleController {
    
    private final PuzzleService puzzleService;

    public PuzzleController(PuzzleService puzzleService) {
        this.puzzleService = puzzleService;
    }

    public Mono<String> execute() {
        return puzzleService.solve();
    }
}
