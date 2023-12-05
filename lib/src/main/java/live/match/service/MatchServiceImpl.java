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
        Match match = new Match(UUID.randomUUID().toString(), new Date(), homeTeam, awayTeam);
        matchRepository.add(match);
        return match;
    }
}
