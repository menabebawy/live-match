package live.match.api;

import live.match.service.Match;
import live.match.service.Team;

public interface LiveScoreboardApi {
    Match startNewMatch(Team homeTeam, Team awayTeam);

    static LiveScoreboardApi createInstant() {
        return new LiveScoreboardApiIml();
    }
}
