package live.match.api;

import live.match.service.Match;
import live.match.service.MatchService;
import live.match.service.Team;

public class LiveScoreboardApiImpl implements LiveScoreboardApi {
    private final MatchService matchService;

    public LiveScoreboardApiImpl(MatchService matchService) {
        this.matchService = matchService;
    }

    @Override
    public Match startNewMatch(Team homeTeam, Team awayTeam) throws StartNewMatchException {
        return matchService.start(homeTeam, awayTeam);
    }
}
