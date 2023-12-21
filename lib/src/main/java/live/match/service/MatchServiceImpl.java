package live.match.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

class MatchServiceImpl implements MatchService {
    private final Scoreboard scoreboard;

    MatchServiceImpl(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    @Override
    public Match start(String homeTeamName, String awayTeamName) throws StartNewMatchException {
        validateTeamsAvailability(homeTeamName, awayTeamName);
        Team homeTeam = new Team(homeTeamName.trim());
        Team awayTeam = new Team(awayTeamName.trim());
        Match match = new Match(UUID.randomUUID().toString(), System.nanoTime(), homeTeam, awayTeam);
        return scoreboard.addMatch(match);
    }

    private void validateTeamsAvailability(String homeTeamName, String awayTeamName) throws StartNewMatchException {
        List<String> occupiedTeamsList = getOccupiedTeamsNames();
        if (occupiedTeamsList.contains(homeTeamName)) {
            throw new StartNewMatchException(homeTeamName + " is currently playing another match");
        }

        if (occupiedTeamsList.contains(awayTeamName)) {
            throw new StartNewMatchException(homeTeamName + " is currently playing another match");
        }
    }

    @Override
    public Match update(String id,
                        int homeTeamScore,
                        int awayTeamScore) throws InvalidMatchStateException, MatchNotFoundException {
        Match match = getMatchByIdOrThrowException(id);

        if (areScoresLessThanCurrent(homeTeamScore, awayTeamScore, match)) {
            throw new InvalidMatchStateException("Value is less than current");
        }

        match.setTeamsScores(homeTeamScore, awayTeamScore);
        scoreboard.updateMatch(match);
        return match;
    }

    private static boolean areScoresLessThanCurrent(int homeTeamScore, int awayTeamScore, Match match) {
        return match.getHomeTeamScore() > homeTeamScore || match.getAwayTeamScore() > awayTeamScore;
    }

    @Override
    public void finish(String id) throws MatchNotFoundException {
        getMatchByIdOrThrowException(id);
        scoreboard.removeMatchById(id);
    }

    private Match getMatchByIdOrThrowException(String id) throws MatchNotFoundException {
        return scoreboard.getOptionalMatch(id)
                .orElseThrow(() -> new MatchNotFoundException("Match id: " + id + "is not found"));
    }

    @Override
    public Scoreboard getSortedScoreboard() {
        return scoreboard;
    }

    private List<String> getOccupiedTeamsNames() {
        return scoreboard.getMatchList().stream()
                .flatMap(match -> Stream.of(match.getHomeTeam().name(), match.getAwayTeam().name()))
                .toList();
    }
}