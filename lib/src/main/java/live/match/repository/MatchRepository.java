package live.match.repository;

import live.match.service.Match;

import java.util.Optional;

public interface MatchRepository {
    void add(Match match);

    void update(Match match);

    Optional<Match> fetchById(String id);

    Optional<Match> isTeamPlayingNow(String teamName);

    static MatchRepository createInstant() {
        return new MatchRepositoryImpl();
    }
}
