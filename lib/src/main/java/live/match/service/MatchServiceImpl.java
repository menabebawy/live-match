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
    public Match start(String homeTeamName, String awayTeamName) throws StartNewMatchException {
        if (matchRepository.isTeamPlayingNow(homeTeamName).isPresent()) {
            throw new StartNewMatchException(homeTeamName + " is currently playing another match");
        }

        if (matchRepository.isTeamPlayingNow(awayTeamName).isPresent()) {
            throw new StartNewMatchException(homeTeamName + " is currently playing another match");
        }

        Team homeTeam = new Team(UUID.randomUUID().toString(), homeTeamName);
        Team awayTeam = new Team(UUID.randomUUID().toString(), awayTeamName);
        Match match = new Match(UUID.randomUUID().toString(), new Date(), homeTeam, awayTeam);
        matchRepository.add(match);
        return match;
    }
}
