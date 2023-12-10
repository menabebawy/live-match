package live.match.service;

import live.match.api.InvalidMatchStateException;
import live.match.api.MatchNotFoundException;
import live.match.api.Scoreboard;
import live.match.api.StartNewMatchException;

public interface MatchService {
    Match start(String homeTeamName, String awayTeamName) throws StartNewMatchException;

    Match update(String id,
                 int homeTeamScore,
                 int awayTeamScore) throws InvalidMatchStateException, MatchNotFoundException;

    Match finish(String id) throws InvalidMatchStateException, MatchNotFoundException;

    Scoreboard createSortedScoreboard();

    static MatchService createInstant() {
        return new MatchServiceImpl();
    }
}