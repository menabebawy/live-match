package live.match.service;

public interface MatchService {
    Match start(String homeTeamName, String awayTeamName) throws StartNewMatchException;

    Match update(String id,
                 int homeTeamScore,
                 int awayTeamScore) throws MatchNotFoundException, InvalidMatchStateException;

    void finish(String id) throws MatchNotFoundException;

    Scoreboard getSortedScoreboard();

    static MatchService createInstance() {
        return new MatchServiceImpl();
    }
}