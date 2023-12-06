package live.match.api;

import live.match.service.Match;
import live.match.service.MatchService;

public interface LiveScoreboardApi {
    Match startNewMatch(String homeTeamName, String awayTeamName) throws StartNewMatchException;

    Scoreboard createScoreboard();

    static LiveScoreboardApi createInstant() {
        return new LiveScoreboardApiImpl(MatchService.createInstant());
    }
}
