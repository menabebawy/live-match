package live.match.api;

import live.match.service.Match;
import live.match.service.MatchService;

public interface LiveScoreboardApi {
    Match startNewMatch(String homeTeamName, String awayTeamName) throws StartNewMatchException;

    Match updateMatch(String id,
                      int homeTeamScore,
                      int awayTeamScore) throws InvalidMatchStateException, MatchNotFoundException;

    Match finishMatch(String id) throws InvalidMatchStateException, MatchNotFoundException;

    Scoreboard createScoreboard();

    static LiveScoreboardApi createInstance() {
        return new LiveScoreboardApiImpl(MatchService.createInstance());
    }
}
