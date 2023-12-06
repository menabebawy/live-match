package live.match.service;

import java.util.Date;
import java.util.UUID;

class MockMatch {
    static Match createStartedMatchInstant() {
        return new Match(UUID.randomUUID().toString(),
                         new Date(),
                         new Team("Home"),
                         new Team("Away"),
                         MatchService.createInstant());
    }
}