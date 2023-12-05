package live.match.repository;

import live.match.service.Match;

import java.util.Optional;

public interface MatchRepository {
    void add(Match match);

    Optional<Match> fetchById(String id);

    static MatchRepository createInstant() {
        return new MatchRepositoryImpl();
    }
}
