package live.match.api;

import live.match.service.Match;
import live.match.service.MatchService;
import live.match.service.Scoreboard;

public class LiveScoreboardApiImpl implements LiveScoreboardApi {
    private final MatchService matchService;

    LiveScoreboardApiImpl(MatchService matchService) {
        this.matchService = matchService;
    }

    @Override
    public Match startNewMatch(String homeTeamName, String awayTeamName) throws StartNewMatchException {
        validateTeamName(homeTeamName);
        validateTeamName(awayTeamName);
        return matchService.start(homeTeamName, awayTeamName);
    }

    private static void validateTeamName(String name) throws StartNewMatchException {
        if (name == null || name.trim().isBlank()) {
            throw new StartNewMatchException("Team name should not be null or empty");
        }
    }

    @Override
    public Match updateMatch(String id,
                             int homeTeamScore,
                             int awayTeamScore) throws InvalidMatchStateException, MatchNotFoundException {
        validateId(id);
        validateScores(homeTeamScore, awayTeamScore);
        return matchService.update(id, homeTeamScore, awayTeamScore);
    }

    private static void validateId(String id) throws InvalidMatchStateException {
        if (id == null || id.trim().isBlank()) {
            throw new InvalidMatchStateException("id should not be null or blank");
        }
    }

    private static void validateScores(int homeTeamScore, int awayTeamScore) throws InvalidMatchStateException {
        if (homeTeamScore < 0 || awayTeamScore < 0) {
            throw new InvalidMatchStateException("score less than zero is not allowed");
        }
    }

    @Override
    public Match finishMatch(String id) throws InvalidMatchStateException, MatchNotFoundException {
        validateId(id);
        return matchService.finish(id);
    }

    @Override
    public Scoreboard createScoreboard() {
        return matchService.createSortedScoreboard();
    }
}
