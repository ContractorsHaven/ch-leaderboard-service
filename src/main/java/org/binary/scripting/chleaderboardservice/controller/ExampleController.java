package org.binary.scripting.chleaderboardservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.binary.scripting.chleaderboardservice.dto.ExampleRequest;
import org.binary.scripting.chleaderboardservice.dto.ChLeaderboardServiceEvent;
import org.binary.scripting.chleaderboardservice.service.ExampleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/v1/leaderboard")
@RequiredArgsConstructor
public class ExampleController {

    private final ExampleService exampleService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<ChLeaderboardServiceEvent> handleRequest(@RequestBody ExampleRequest request) {
        log.info("Received request: {}", request);
        return exampleService.processRequest(request);
    }
}
