package com.lumu.puzzle_decoder.mapper;

import com.lumu.puzzle_decoder.dto.FragmentResponse;
import com.lumu.puzzle_decoder.model.PuzzleFragment;

public class FragmentMapper {

    public PuzzleFragment toDomain(
            FragmentResponse response) {
        if (response == null) {
            throw new IllegalArgumentException(
                    "Fragment response cannot be null"
            );
        }

        if (response.text() == null) {
            throw new IllegalArgumentException(
                    "Fragment text cannot be null"
            );
        }

        return new PuzzleFragment(
                response.id(),
                response.index(),
                response.text()
        );
    }
}
