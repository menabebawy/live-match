package live.match.service;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

public interface MatchService {
    Match start(String homeTeamName, String awayTeamName) throws StartNewMatchException;

    Match update(String id,
                 int homeTeamScore,
                 int awayTeamScore) throws MatchNotFoundException, InvalidMatchStateException;

    void finish(String id) throws MatchNotFoundException;

    Scoreboard getSortedScoreboard();

    static MatchService createInstance() {
        Comparator<Match> comparator = Comparator.comparing(Match::getScore)
                .thenComparing(Match::getStartedAt)
                .reversed();
        return new MatchServiceImpl(new Scoreboard(comparator, new ConcurrentHashMap<>()));
    }
}