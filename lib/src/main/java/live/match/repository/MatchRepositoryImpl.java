package live.match.repository;

import live.match.service.Match;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class MatchRepositoryImpl implements MatchRepository {
    private final Map<String, Match> matchMap;

    MatchRepositoryImpl() {
        matchMap = new HashMap<>();
    }

    @Override
    public void add(Match match) {
        matchMap.put(match.getId(), match);
    }

    @Override
    public Optional<Match> isTeamPlayingNow(String teamId) {
        return matchMap.values().stream()
                .filter(match -> !match.isFinished())
                .filter(match -> match.getHomeTeam().id().equals(teamId) || match.getAwayTeam().id().equals(teamId))
                .findFirst();
    }
}
