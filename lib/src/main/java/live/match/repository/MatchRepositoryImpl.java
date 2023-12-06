package live.match.repository;

import live.match.service.Match;

import java.util.HashMap;
import java.util.List;
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
    public void update(Match match) {
        add(match);
    }

    @Override
    public Optional<Match> fetchById(String id) {
        Match match = matchMap.get(id);
        return match != null ? Optional.of(match) : Optional.empty();
    }

    @Override
    public List<Match> fetchAllInProgress() {
        return matchMap.values().stream()
                .filter(match -> !match.isFinished())
                .toList();
    }

    @Override
    public Optional<Match> isTeamPlayingNow(String teamName) {
        return matchMap.values().stream()
                .filter(match -> !match.isFinished())
                .filter(match -> match.getHomeTeam().name().equalsIgnoreCase(teamName) ||
                        match.getAwayTeam().name().equalsIgnoreCase(teamName))
                .findFirst();
    }
}