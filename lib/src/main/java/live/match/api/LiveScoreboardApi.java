package live.match.api;

import live.match.service.*;

public interface LiveScoreboardApi {

    /**
     * Returns started {@code Match}.
     *
     * <p> Use this method to start a new match. It takes two parameters,
     * {@code homeTeamName} and {@code awayTeamName} as a {@code String}.
     * It returns {@code Match} if params are valid and both of two teams are currently
     * not attached to any in progress match. Otherwise, it throws an exception.
     *
     * @param homeTeamName home team's name; must be nonnull and not blank
     * @param awayTeamName away team's name; must be nonnull and not blank
     * @return the started Match between home team and away team
     * @throws IllegalArgumentException if team's name is either null or blank
     * @throws StartNewMatchException if either one of two teams or both of them are currently occupied and
     *         are playing another match at the moment
     */
    Match startNewMatch(String homeTeamName,
                        String awayTeamName) throws IllegalArgumentException, StartNewMatchException;

    /**
     * Returns updated {@code Match}.
     *
     * <p> Use this method to update in progress match.
     * It takes the most updated teams' scores {@code homeTeamScore}
     * and {@code awayTeamScore} as {@code int}. It returns updated {@code Match}.
     * It throws exception if params are not valid or reliable.
     *
     * @param id id of the desired match to update
     * @param homeTeamScore most recent score of home team
     * @param awayTeamScore most recent score of away team
     * @return the updated {@code Match}.
     * @throws IllegalArgumentException if id is null or blank, or score is less than 0
     * @throws MatchNotFoundException if id is not found in DB
     * @throws InvalidMatchStateException if passed score is less than current. For instance, home team
     *         already got 3 goals and the updated score is 2.
     */
    Match updateMatch(String id,
                      int homeTeamScore,
                      int awayTeamScore) throws IllegalArgumentException, MatchNotFoundException, InvalidMatchStateException;

    /**
     * Returns finished {@code Match}
     *
     * <p> Use this method to finish in progress match.
     * It takes the id of desired match to finish.
     * It returns most update {@code Match}.
     * It is not applicable to finish match that not has been
     * started yet.
     *
     * @param id of the desired match to update
     * @return the finished {@code Match}
     * @throws IllegalArgumentException if id is null or blank
     * @throws MatchNotFoundException if id is not found in DB
     */
    Match finishMatch(String id) throws IllegalArgumentException, MatchNotFoundException;
    
    /**
     * Return {@code Scoreboard} in which all in progress matches there.
     *
     * <p> Use this method to get scoreboard where you can easily call
     * {@code getSummery} that shows sorted in progress matches by score and most
     * started.
     *
     * @return {@code Scoreboard}
     */
    Scoreboard createScoreboard();

    static LiveScoreboardApi createInstance() {
        return new LiveScoreboardApiImpl(MatchService.createInstance());
    }
}