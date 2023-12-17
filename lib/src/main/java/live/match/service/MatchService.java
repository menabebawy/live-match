package live.match.service;

import live.match.api.InvalidMatchStateException;
import live.match.api.MatchNotFoundException;
import live.match.api.StartNewMatchException;

public interface MatchService {
    Match start(String homeTeamName, String awayTeamName) throws StartNewMatchException;

    Match update(String id,
                 int homeTeamScore,
                 int awayTeamScore) throws MatchNotFoundException, InvalidMatchStateException;

    Match finish(String id) throws MatchNotFoundException;

    Scoreboard createSortedScoreboard();

    static MatchService createInstance() {
        return new MatchServiceImpl();
    }
}