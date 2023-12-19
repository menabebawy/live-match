package live.match.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

class MatchServiceImpl implements MatchService {
    private final Map<String, Match> matchMap = new HashMap<>();

    @Override
    public Match start(String homeTeamName, String awayTeamName) throws StartNewMatchException {
        validateTeamsAvailability(homeTeamName, awayTeamName);
        Team homeTeam = new Team(homeTeamName.trim());
        Team awayTeam = new Team(awayTeamName.trim());
        Match match = new Match(UUID.randomUUID().toString(), System.nanoTime(), homeTeam, awayTeam);
        matchMap.put(match.getId(), match);
        return match;
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

        matchMap.put(match.getId(), match);

        return match;
    }

    private static boolean areScoresLessThanCurrent(int homeTeamScore, int awayTeamScore, Match match) {
        return match.getHomeTeamScore() > homeTeamScore || match.getAwayTeamScore() > awayTeamScore;
    }

    @Override
    public void finish(String id) throws MatchNotFoundException {
        getMatchByIdOrThrowException(id);
        matchMap.remove(id);
    }

    private Match getMatchByIdOrThrowException(String id) throws MatchNotFoundException {
        Match match = matchMap.get(id);
        if (match == null) {
            throw new MatchNotFoundException("Match id: " + id + "is not found");
        }
        return match;
    }

    @Override
    public Scoreboard createSortedScoreboard() {
        List<Match> inProgressMatches = matchMap.values().stream()
                .sorted(Comparator.comparing(Match::getScore).thenComparing(Match::getStartedAt).reversed())
                .toList();
        return new Scoreboard(inProgressMatches);
    }

    private List<String> getOccupiedTeamsNames() {
        return matchMap.values().stream()
                .flatMap(match -> Stream.of(match.getHomeTeam().name(), match.getAwayTeam().name()))
                .toList();
    }
}