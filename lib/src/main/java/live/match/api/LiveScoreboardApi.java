package live.match.api;

import live.match.service.Match;
import live.match.service.MatchService;
import live.match.service.Scoreboard;

import javax.naming.OperationNotSupportedException;

public interface LiveScoreboardApi {
    Match startNewMatch(String homeTeamName, String awayTeamName) throws StartNewMatchException;

    Match updateMatch(String id,
                      int homeTeamScore,
                      int awayTeamScore) throws InvalidMatchStateException, MatchNotFoundException;

    Match finishMatch(String id) throws OperationNotSupportedException, InvalidMatchStateException, MatchNotFoundException;

    Scoreboard createScoreboard();

    static LiveScoreboardApi createInstance() {
        return new LiveScoreboardApiImpl(MatchService.createInstance());
    }
}
