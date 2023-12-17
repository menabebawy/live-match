package live.match.api;

import live.match.service.Match;
import live.match.service.MatchService;
import live.match.service.Scoreboard;

public interface LiveScoreboardApi {
    Match startNewMatch(String homeTeamName,
                        String awayTeamName) throws IllegalArgumentException, StartNewMatchException;

    Match updateMatch(String id,
                      int homeTeamScore,
                      int awayTeamScore) throws IllegalArgumentException, MatchNotFoundException, InvalidMatchStateException;

    Match finishMatch(String id) throws IllegalArgumentException, MatchNotFoundException;

    Scoreboard createScoreboard();

    static LiveScoreboardApi createInstance() {
        return new LiveScoreboardApiImpl(MatchService.createInstance());
    }
}
