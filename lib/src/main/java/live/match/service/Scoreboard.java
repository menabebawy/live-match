package live.match.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class Scoreboard {
    private final Map<String, Match> matchMap;
    private final Comparator<Match> matchComparator;


    Scoreboard(Comparator<Match> matchComparator) {
        matchMap = new ConcurrentHashMap<>();
        this.matchComparator = matchComparator;
    }

    public String getSummary() {
        int index = 0;
        StringBuilder summeryBuilder = new StringBuilder();
        boolean newLine = false;
        for (Match match : getMatchList()) {
            if (newLine) {
                summeryBuilder.append(System.lineSeparator());
            }
            newLine = true;
            summeryBuilder.append(generateSummeryRow(++index, match));
        }
        return summeryBuilder.toString();
    }

    private static String generateSummeryRow(int index, Match match) {
        return index + ". " +
                match.getHomeTeam().name() + " " + match.getHomeTeamScore() +
                " - " +
                match.getAwayTeam().name() + " " + match.getAwayTeamScore();
    }

    public List<Match> getMatchList() {
        return matchMap.values().stream()
                .sorted(matchComparator)
                .toList();
    }

    Optional<Match> getOptionalMatch(String id) {
        Match match = matchMap.get(id);
        return match == null ? Optional.empty() : Optional.of(match);
    }

    void addMatch(Match match) {
        matchMap.putIfAbsent(match.getId(), match);
    }

    void updateMatch(Match updatedMatch) {
        matchMap.put(updatedMatch.getId(), updatedMatch);
    }

    void removeMatchById(String id) {
        matchMap.remove(id);
    }

}