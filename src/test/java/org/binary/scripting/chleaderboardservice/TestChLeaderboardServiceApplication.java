package org.binary.scripting.chleaderboardservice;

import org.springframework.boot.SpringApplication;

public class TestChLeaderboardServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(ChLeaderboardServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
