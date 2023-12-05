package live.match.service;

import live.match.api.StartNewMatchException;

public interface MatchService {
    Match start(Team homeTeam, Team awayTeam) throws StartNewMatchException;
}
