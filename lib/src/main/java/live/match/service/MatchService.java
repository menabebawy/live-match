package live.match.service;

import live.match.api.InvalidMatchStateException;
import live.match.api.StartNewMatchException;
import live.match.repository.MatchRepository;

public interface MatchService {
    Match start(String homeTeamName, String awayTeamName) throws StartNewMatchException;

    void update(String id, int homeTeamScore, int awayTeamScore) throws InvalidMatchStateException;

    void finish(String id) throws InvalidMatchStateException;

    static MatchService createInstant() {
        return new MatchServiceImpl(MatchRepository.createInstant());
    }
}