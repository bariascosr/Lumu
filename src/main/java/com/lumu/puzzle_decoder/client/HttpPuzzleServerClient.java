package com.lumu.puzzle_decoder.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumu.puzzle_decoder.dto.FragmentResponse;
import com.lumu.puzzle_decoder.exception.FragmentClientException;
import com.lumu.puzzle_decoder.mapper.FragmentMapper;
import com.lumu.puzzle_decoder.model.PuzzleFragment;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class HttpPuzzleServerClient implements PuzzleServerClient {

    private static final String FRAGMENT_PATH = "/fragment?id=";

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(2);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final FragmentMapper mapper;
    private final String baseUrl;
    
    public HttpPuzzleServerClient(HttpClient httpClient, 
        ObjectMapper objectMapper, 
        FragmentMapper mapper,
        String baseUrl) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.mapper = mapper;
        this.baseUrl = baseUrl;
    }

    @Override
    public Mono<PuzzleFragment> fetchFragment(int id) {
        return Mono.defer(
                () -> sendRequest(id));

    }

    @Override
    public Flux<PuzzleFragment> fetchFragments(List<Integer> ids) {
    
        return Flux.fromIterable(ids)
                    .flatMap(this::fetchFragment);
    }

    private Mono<PuzzleFragment> sendRequest(int id) {
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl
                                        + FRAGMENT_PATH
                                        + id))
                        .timeout(REQUEST_TIMEOUT)
                        .GET()
                        .build();

        return Mono.fromFuture(httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()))
                .flatMap(this::processResponse);
    }

    private Mono<PuzzleFragment> processResponse(HttpResponse<String> response) {
        
        if (response.statusCode() < 200 || response.statusCode() >= 300) {

            return Mono.error(new FragmentClientException("Server returned HTTP "
                            + response.statusCode()));
        }

        return Mono.fromCallable(
                        () -> deserialize(response.body()))
                        .map(mapper::toDomain)
                        .onErrorMap(exception -> {
                            if (exception instanceof FragmentClientException) {
                                return exception;
                            }

                            return new FragmentClientException(
                                    "Unable to process fragment response",
                                    exception
                            );
                        }
                );
    }

    private FragmentResponse deserialize(String body) {
        try {
            return objectMapper.readValue(body, FragmentResponse.class);
        } catch (Exception exception) {
            throw new FragmentClientException("Invalid fragment JSON",
                    exception
            );
        }
    }
    
}
