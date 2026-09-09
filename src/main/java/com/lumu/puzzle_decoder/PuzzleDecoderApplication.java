package com.lumu.puzzle_decoder;

import java.net.http.HttpClient;
import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumu.puzzle_decoder.client.HttpPuzzleServerClient;
import com.lumu.puzzle_decoder.client.PuzzleServerClient;
import com.lumu.puzzle_decoder.controller.PuzzleController;
import com.lumu.puzzle_decoder.mapper.FragmentMapper;
import com.lumu.puzzle_decoder.service.PuzzleService;

public class PuzzleDecoderApplication {

	 private static final String SERVER_URL = "http://localhost:8080";

    public static void main(String[] args) {

        try {
            HttpClient httpClient = HttpClient.newBuilder()
                                        .version(HttpClient.Version.HTTP_1_1)
                                        .connectTimeout(Duration.ofSeconds(1))
                                        .build();


            ObjectMapper objectMapper = new ObjectMapper();

            FragmentMapper fragmentMapper = new FragmentMapper();

            PuzzleServerClient puzzleServerClient = new HttpPuzzleServerClient(
                            httpClient,
                            objectMapper,
                            fragmentMapper,
                            SERVER_URL);

            PuzzleService puzzleService = new PuzzleService(puzzleServerClient);

            PuzzleController puzzleController = new PuzzleController(puzzleService);

            String message = puzzleController.execute().block();

            System.out.println();
            System.out.println("Puzzle solved!");
            System.out.println("Message: " + message);

        } catch (Exception exception) {
            System.err.println("Application failed: " + exception.getMessage());
            System.exit(1);
        }
    }

}
