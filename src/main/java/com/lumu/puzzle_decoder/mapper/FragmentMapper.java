package com.lumu.puzzle_decoder.mapper;

import com.lumu.puzzle_decoder.dto.FragmentResponseDto;
import com.lumu.puzzle_decoder.model.PuzzleFragment;

public class FragmentMapper {

    public PuzzleFragment toDomain(FragmentResponseDto response) {
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
