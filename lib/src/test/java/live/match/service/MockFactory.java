package live.match.service;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MockFactory {
    public static Match createMatchInstance() {
        return new Match(UUID.randomUUID().toString(),
                         System.nanoTime(),
                         new Team("HomeTeam"),
                         new Team("AwayTeam"));
    }

    public static Match updatedMatchInstance(int homeTeamScore, int awayTeamScore) {
        Match match = createMatchInstance();
        match.setTeamsScores(homeTeamScore, awayTeamScore);
        return match;
    }

    public static Scoreboard createEmptyScoreboardInstance() {
        return new Scoreboard(defaultComparator(), new ConcurrentHashMap<>());
    }

    public static Scoreboard createOneMatchScoreboardInstance() {
        Match match = new Match(UUID.randomUUID().toString(),
                                System.nanoTime(),
                                new Team("Mexico"),
                                new Team("Canada"));
        match.setTeamsScores(5, 1);
        Map<String, Match> mock = new ConcurrentHashMap<>();
        mock.put(match.getId(), match);
        return new Scoreboard(defaultComparator(), mock);
    }

    static Comparator<Match> defaultComparator() {
        return Comparator.comparing(Match::getScore)
                .thenComparing(Match::getStartedAt)
                .reversed();
    }

}