package live.match.api;

import live.match.service.Match;
import live.match.service.MatchService;

public class LiveScoreboardApiImpl implements LiveScoreboardApi {
    private final MatchService matchService;

    LiveScoreboardApiImpl(MatchService matchService) {
        this.matchService = matchService;
    }

    @Override
    public Match startNewMatch(String homeTeamName, String awayTeamName) throws StartNewMatchException {
        validateParameters(homeTeamName, awayTeamName);
        return matchService.start(homeTeamName, awayTeamName);
    }

    @Override
    public Match updateMatch(String id, int homeTeamScore, int awayTeamScore) throws InvalidMatchStateException {
        return matchService.update(id, homeTeamScore, awayTeamScore);
    }

    @Override
    public Scoreboard createScoreboard() {
        return matchService.createSoretedScoreboard();
    }

    private void validateParameters(String homeTeamName, String awayTeamName) throws StartNewMatchException {
        if (homeTeamName == null || awayTeamName == null) {
            throw new StartNewMatchException("team/s names should not be null");
        }

        if (homeTeamName.isBlank() || awayTeamName.isBlank()) {
            throw new StartNewMatchException("team/s names should not be blank");
        }
    }
}
