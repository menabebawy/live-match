package live.match.api;

import live.match.service.Match;
import live.match.service.MatchService;
import live.match.service.Team;

public interface LiveScoreboardApi {
    Match startNewMatch(Team homeTeam, Team awayTeam) throws StartNewMatchException;

    static LiveScoreboardApi createInstant() {
        return new LiveScoreboardApiImpl(MatchService.createInstant());
    }
}
