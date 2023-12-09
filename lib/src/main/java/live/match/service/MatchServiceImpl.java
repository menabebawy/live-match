package live.match.service;

import live.match.api.InvalidMatchStateException;
import live.match.api.MatchNotFoundException;
import live.match.api.Scoreboard;
import live.match.api.StartNewMatchException;

import java.util.*;

class MatchServiceImpl implements MatchService {
    private final Map<String, Match> matchMap = new HashMap<>();

    @Override
    public Match start(String homeTeamName, String awayTeamName) throws StartNewMatchException {
        if (isTeamPlayingNow(homeTeamName).isPresent()) {
            throw new StartNewMatchException(homeTeamName + " is currently playing another match");
        }

        if (isTeamPlayingNow(awayTeamName).isPresent()) {
            throw new StartNewMatchException(homeTeamName + " is currently playing another match");
        }

        Team homeTeam = new Team(homeTeamName);
        Team awayTeam = new Team(awayTeamName);
        Match match = new Match(UUID.randomUUID().toString(), System.nanoTime(), homeTeam, awayTeam, this);
        matchMap.put(match.getId(), match);
        return match;
    }

    @Override
    public Match update(String id,
                        int homeTeamScore,
                        int awayTeamScore) throws InvalidMatchStateException, MatchNotFoundException {
        Match match = getMatchByIdOrThrowException(id);

        if (match.isFinished()) {
            throw new InvalidMatchStateException("Update finished match is not allowed");
        }

        if (match.getHomeTeamScore() > homeTeamScore || match.getAwayTeamScore() > awayTeamScore) {
            throw new InvalidMatchStateException("Value is less than current");
        }

        match.setTeamsScores(homeTeamScore, awayTeamScore);

        return matchMap.put(match.getId(), match);
    }

    @Override
    public Match finish(String id) throws InvalidMatchStateException, MatchNotFoundException {
        Match match = getMatchByIdOrThrowException(id);

        if (match.isFinished()) {
            throw new InvalidMatchStateException("Match id: " + id + " is already finished");
        }

        match.setFinished();

        return matchMap.put(match.getId(), match);
    }

    private Match getMatchByIdOrThrowException(String id) throws MatchNotFoundException {
        Match match = matchMap.get(id);
        if (match == null) {
            throw new MatchNotFoundException("Match id: " + id + "is not found");
        }
        return match;
    }

    @Override
    public Scoreboard createSoretedScoreboard() {
        List<Match> inProgressMatches = getInProgressMatches().stream()
                .sorted(Comparator.comparing(Match::getScore).thenComparing(Match::getStartedAt).reversed())
                .toList();
        return new Scoreboard(inProgressMatches);
    }

    private Optional<Match> isTeamPlayingNow(String teamName) {
        return getInProgressMatches().stream()
                .filter(match -> match.getHomeTeam().name().equalsIgnoreCase(teamName) ||
                        match.getAwayTeam().name().equalsIgnoreCase(teamName))
                .findFirst();
    }

    private List<Match> getInProgressMatches() {
        return matchMap.values().stream().filter(match -> !match.isFinished()).toList();
    }
}