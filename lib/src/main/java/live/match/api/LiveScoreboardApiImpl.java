package live.match.api;

import live.match.service.InvalidMatchStateException;
import live.match.service.Match;
import live.match.service.MatchNotFoundException;
import live.match.service.MatchService;
import live.match.service.Scoreboard;
import live.match.service.StartNewMatchException;

class LiveScoreboardApiImpl implements LiveScoreboardApi {
    private final MatchService matchService;

    LiveScoreboardApiImpl(MatchService matchService) {
        this.matchService = matchService;
    }

    @Override
    public Match startNewMatch(String homeTeamName,
                               String awayTeamName) throws IllegalArgumentException, StartNewMatchException {
        validateTeamName(homeTeamName);
        validateTeamName(awayTeamName);
        return matchService.start(homeTeamName, awayTeamName);
    }

    private static void validateTeamName(String name) throws IllegalArgumentException {
        if (name == null || name.trim().isBlank()) {
            throw new IllegalArgumentException("Team name should not be null or empty");
        }
    }

    @Override
    public Match updateMatch(String id,
                             int homeTeamScore,
                             int awayTeamScore) throws IllegalArgumentException, MatchNotFoundException, InvalidMatchStateException {
        validateId(id);
        validateScore(homeTeamScore);
        validateScore(awayTeamScore);
        return matchService.update(id, homeTeamScore, awayTeamScore);
    }

    private static void validateId(String id) throws IllegalArgumentException {
        if (id == null || id.trim().isBlank()) {
            throw new IllegalArgumentException("id should not be null or blank");
        }
    }

    private static void validateScore(int score) throws IllegalArgumentException {
        if (score < 0) {
            throw new IllegalArgumentException("score less than zero is not allowed");
        }
    }

    @Override
    public Match finishMatch(String id) throws IllegalArgumentException, MatchNotFoundException {
        validateId(id);
        return matchService.finish(id);
    }

    @Override
    public Scoreboard createScoreboard() {
        return matchService.createSortedScoreboard();
    }
}
