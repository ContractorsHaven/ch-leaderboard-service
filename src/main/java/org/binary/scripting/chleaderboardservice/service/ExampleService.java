package org.binary.scripting.chleaderboardservice.service;

import org.binary.scripting.chleaderboardservice.dto.ExampleRequest;
import org.binary.scripting.chleaderboardservice.dto.ChLeaderboardServiceEvent;
import reactor.core.publisher.Mono;

public interface ExampleService {

    Mono<ChLeaderboardServiceEvent> processRequest(ExampleRequest request);
}
