package live.match.service;

import live.match.api.StartNewMatchException;
import live.match.repository.MatchRepository;

public interface MatchService {
    Match start(String homeTeamName, String awayTeamName) throws StartNewMatchException;

    static MatchService createInstant() {
        return new MatchServiceImpl(MatchRepository.createInstant());
    }
}
