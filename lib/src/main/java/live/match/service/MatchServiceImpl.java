package live.match.service;

import live.match.api.InvalidMatchStateException;
import live.match.api.Scoreboard;
import live.match.api.StartNewMatchException;
import live.match.repository.MatchRepository;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

class MatchServiceImpl implements MatchService {
    private final MatchRepository matchRepository;

    MatchServiceImpl(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Override
    public Match start(String homeTeamName, String awayTeamName) throws StartNewMatchException {
        if (matchRepository.isTeamPlayingNow(homeTeamName).isPresent()) {
            throw new StartNewMatchException(homeTeamName + " is currently playing another match");
        }

        if (matchRepository.isTeamPlayingNow(awayTeamName).isPresent()) {
            throw new StartNewMatchException(homeTeamName + " is currently playing another match");
        }

        Team homeTeam = new Team(homeTeamName);
        Team awayTeam = new Team(awayTeamName);
        Match match = new Match(UUID.randomUUID().toString(), System.nanoTime(), homeTeam, awayTeam, this);
        matchRepository.add(match);
        return match;
    }

    @Override
    public Match update(String id, int homeTeamScore, int awayTeamScore) throws InvalidMatchStateException {
        Match match = getMatchByIdOrThrowException(id);

        if (match.isFinished()) {
            throw new InvalidMatchStateException("Update finshed match is not allowed");
        }

        if (match.getHomeTeamScore() > homeTeamScore || match.getAwayTeamScore() > awayTeamScore) {
            throw new InvalidMatchStateException("Value is less than current");
        }

        match.setTeamsScores(homeTeamScore, awayTeamScore);

        matchRepository.update(match);

        return match;
    }

    @Override
    public void finish(String id) throws InvalidMatchStateException {
        Match match = getMatchByIdOrThrowException(id);

        if (match.isFinished()) {
            throw new InvalidMatchStateException("Match id:" + id + " is already finished");
        }

        match.setFinished();

        matchRepository.update(match);
    }

    private Match getMatchByIdOrThrowException(String id) throws InvalidMatchStateException {
        return matchRepository.fetchById(id)
                .orElseThrow(() -> new InvalidMatchStateException("Match id: " + id + "is not found"));
    }

    @Override
    public Scoreboard createSoretedScoreboard() {
        List<Match> inProgressMatches = matchRepository.fetchAllInProgress().stream()
                .sorted(Comparator.comparing(Match::getScore).thenComparing(Match::getStartedAt).reversed())
                .toList();
        return new Scoreboard(inProgressMatches);
    }
}