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
        validateParameters(homeTeam, awayTeam);
        return matchService.start(homeTeam, awayTeam);
    }

    private void validateParameters(Team homeTeam, Team awayTeam) throws StartNewMatchException {
        if (homeTeam == null || awayTeam == null) {
            throw new StartNewMatchException("team/s should not be null");
        }

        if (homeTeam.name() == null || awayTeam.name() == null) {
            throw new StartNewMatchException("team/s name should not be null");
        }

        if (homeTeam.name().isBlank() || awayTeam.name().isBlank()) {
            throw new StartNewMatchException("team/s name should not be blank");
        }
    }
}
