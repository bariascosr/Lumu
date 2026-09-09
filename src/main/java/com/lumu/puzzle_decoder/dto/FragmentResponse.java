package com.lumu.puzzle_decoder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FragmentResponse(
        @JsonProperty("id")
        int id,

        @JsonProperty("index")
        int index,

        @JsonProperty("text")
        String text
) {}
