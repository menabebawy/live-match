package live.match.service;

import live.match.api.InvalidMatchStateException;
import live.match.api.StartNewMatchException;
import live.match.repository.MatchRepository;

import java.util.Date;
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
        Match match = new Match(UUID.randomUUID().toString(), new Date(), homeTeam, awayTeam, this);
        matchRepository.add(match);
        return match;
    }

    @Override
    public void update(String id, int homeTeamScore, int awayTeamScore) throws InvalidMatchStateException {
        Match match = getMatchByIdOrThrowException(id);

        if (match.isFinished()) {
            throw new InvalidMatchStateException("Update finshed match is not allowed");
        }

        if (match.getHomeTeamScore() > homeTeamScore || match.getAwayTeamScore() > awayTeamScore) {
            throw new InvalidMatchStateException("Value is less than current");
        }

        match.setTeamsScores(homeTeamScore, awayTeamScore);

        matchRepository.update(match);
    }

    @Override
    public void finish(String id) throws InvalidMatchStateException {
        Match match = getMatchByIdOrThrowException(id);

        if (match.isFinished()) {
            throw new InvalidMatchStateException("Match id:" + id + " is already finished");
        }

        match.setFinished(true);

        matchRepository.update(match);
    }

    private Match getMatchByIdOrThrowException(String id) throws InvalidMatchStateException {
        return matchRepository.fetchById(id)
                .orElseThrow(() -> new InvalidMatchStateException("Match id: " + id + "is not found"));
    }
}