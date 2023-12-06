package live.match.service;

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
    public Match start(Team homeTeam, Team awayTeam) throws StartNewMatchException {
        if (matchRepository.isTeamPlayingNow(homeTeam.id()).isPresent()) {
            throw new StartNewMatchException(homeTeam.name() + " is currently playing another match");
        }

        if (matchRepository.isTeamPlayingNow(awayTeam.id()).isPresent()) {
            throw new StartNewMatchException(homeTeam.name() + " is currently playing another match");
        }

        Match match = new Match(UUID.randomUUID().toString(), new Date(), homeTeam, awayTeam);
        matchRepository.add(match);
        return match;
    }
}
