package live.match.api;

import live.match.service.Match;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class LiveScoreboardApiImplTest {
    LiveScoreboardApi liveScoreboardApi;
    static final String HOME_TEAM_NAME = "HomeTeam";
    static final String AWAY_TEAM_NAME = "AwayTeam";
    static final String SNOW_TEAM_NAME = "SnowTeam";

    @BeforeEach
    void setUp() {
        liveScoreboardApi = LiveScoreboardApi.createInstant();
    }

    @AfterEach
    void tearDown() {
        liveScoreboardApi = null;
    }

    // region start new match
    @Test
    void givenNoNullTeams_whenStartNewMatch_thenMatchStartedInProgressAndScoreZero() throws StartNewMatchException {
        Match match = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME);
        assertNotNull(match);
        assertEquals(0, match.getScore());
        assertFalse(match.isFinished());
    }

    @Test
    void givenTeamsNamesSurroundWhitespace_whenStartNewMatch_thenTrimNames() throws StartNewMatchException {
        Match match = liveScoreboardApi.startNewMatch(" " + HOME_TEAM_NAME + "   ", AWAY_TEAM_NAME);
        assertNotNull(match);
        assertEquals(HOME_TEAM_NAME, match.getHomeTeam().name());
    }

    @Test
    void givenTeamHomeNull_whenStartNewMatch_thenThrowStartNewMatchException() {
        Exception exception = assertThrows(StartNewMatchException.class,
                                           () -> liveScoreboardApi.startNewMatch(null, AWAY_TEAM_NAME));
        assertNotNull(exception);
    }

    @Test
    void givenAwayTeamNull_whenStartNewMatch_thenThrowStartNewMatchException() {
        Exception exception = assertThrows(StartNewMatchException.class,
                                           () -> liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, null));
        assertNotNull(exception);
    }

    @Test
    void givenOccupiedHomeTeam_whenStartNewMatch_thenThrowStartNewMatchException() throws StartNewMatchException {
        liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, SNOW_TEAM_NAME);

        Exception exception = assertThrows(StartNewMatchException.class,
                                           () -> liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME));
        assertNotNull(exception);
    }

    @Test
    void givenOccupiedAwayTeam_whenStartNewMatch_thenThrowStartNewMatchException() throws StartNewMatchException {
        liveScoreboardApi.startNewMatch(SNOW_TEAM_NAME, AWAY_TEAM_NAME);

        Exception exception = assertThrows(StartNewMatchException.class,
                                           () -> liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME));
        assertNotNull(exception);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void givenHomeTeamNameBlank_whenStartNewMatch_thenThrowStartNewMatchException(String blankTeamName) {
        Exception exception = assertThrows(StartNewMatchException.class,
                                           () -> liveScoreboardApi.startNewMatch(blankTeamName, AWAY_TEAM_NAME));
        assertNotNull(exception);
    }

    // endregion

    // region update match

    @Test
    void givenTeamsScoreForFinishedMatch_whenUpdateMatch_thenThrowInvalidMatchStateException() throws InvalidMatchStateException, StartNewMatchException, MatchNotFoundException {
        Match match = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME);
        liveScoreboardApi.finishMatch(match.getId());
        Exception exception = assertThrows(InvalidMatchStateException.class,
                                           () -> liveScoreboardApi.updateMatch(match.getId(), 0, 1));
        assertNotNull(exception);
    }

    @Test
    void givenNotStartedMatchYet_whenUpdateMatch_thenThrowMatchNotFoundException() {
        Exception exception = assertThrows(MatchNotFoundException.class,
                                           () -> liveScoreboardApi.updateMatch("id", 1, 0));
        assertNotNull(exception);
    }

    @Test
    void givenTeamScoreLessThanZero_whenUpdateMatch_thenThrowInvalidMatchStateException() throws StartNewMatchException {
        Match match = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME);
        Exception exception = assertThrows(InvalidMatchStateException.class,
                                           () -> liveScoreboardApi.updateMatch(match.getId(), 2, -1));
        assertNotNull(exception);
    }

    @Test
    void givenScoreLessThanCurrent_whenUpdateMatch_thenThrowInvalidMatchStateException() throws InvalidMatchStateException, StartNewMatchException, MatchNotFoundException {
        Match match = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME);
        liveScoreboardApi.updateMatch(match.getId(), 2, 0);
        Exception exception = assertThrows(InvalidMatchStateException.class,
                                           () -> liveScoreboardApi.updateMatch(match.getId(), 0, 1));
        assertNotNull(exception);
    }

    @Test
    void givenValidScores_whenUpdateMatch_thenNewScoresUpdated() throws InvalidMatchStateException, StartNewMatchException, MatchNotFoundException {
        Match match = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME);
        Match updatedMatch = liveScoreboardApi.updateMatch(match.getId(), 1, 0);
        assertEquals(1, updatedMatch.getHomeTeamScore());
        assertEquals(0, updatedMatch.getAwayTeamScore());
    }

    // endregion

    // region finish match

    @Test
    void givenAlreadyFinishedMatchId_whenFinishMatch_thenThrowsInvalidMatchStateException() {
        Exception exception = assertThrows(MatchNotFoundException.class, () -> liveScoreboardApi.finishMatch("id"));
        assertNotNull(exception);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void givenInvalidMatchId_whenFinishMatch_thenThrowsInvalidMatchStateException(String id) {
        Exception exception = assertThrows(InvalidMatchStateException.class, () -> liveScoreboardApi.finishMatch(id));
        assertNotNull(exception);
    }

    @Test
    void givenNotFoundMatchId_whenFinishMatch_thenThrowsMatchNotFoundException() throws StartNewMatchException, InvalidMatchStateException, MatchNotFoundException {
        Match match = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME);
        liveScoreboardApi.finishMatch(match.getId());
        Exception exception = assertThrows(InvalidMatchStateException.class,
                                           () -> liveScoreboardApi.finishMatch(match.getId()));
        assertNotNull(exception);
    }

    @Test
    void givenInProgressMatchId_whenFinishMatch_thenFinishMatch() throws StartNewMatchException, InvalidMatchStateException, MatchNotFoundException {
        Match match = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME);
        Match finishedMatch = liveScoreboardApi.finishMatch(match.getId());
        assertTrue(finishedMatch.isFinished());
    }

    // endregion

    // region get scoreboard

    @Test
    void givenNonMathsStarted_whenGetScoreboard_thenEmptySummary() {
        Scoreboard scoreboard = liveScoreboardApi.createScoreboard();
        assertEquals(0, scoreboard.mathList().size());
        assertEquals("", scoreboard.getSummary());
    }

    @Test
    void givenOneMatch_whenGetScoreboard_thenSummaryOfOneMatch() throws StartNewMatchException, InvalidMatchStateException, MatchNotFoundException {
        Match match = liveScoreboardApi.startNewMatch("Mexico", "Canada");
        liveScoreboardApi.updateMatch(match.getId(), 1, 0);
        liveScoreboardApi.updateMatch(match.getId(), 1, 1);
        liveScoreboardApi.updateMatch(match.getId(), 2, 1);
        liveScoreboardApi.updateMatch(match.getId(), 3, 1);

        String expectedSummary = "1. Mexico 3 - Canada 1";

        Scoreboard scoreboard = liveScoreboardApi.createScoreboard();

        assertEquals(expectedSummary, scoreboard.getSummary());
    }

    @Test
    void givenOneFinishedMatch_whenGetScoreboard_thenEmptySummary() throws InvalidMatchStateException, StartNewMatchException, MatchNotFoundException {
        Match match = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME);
        liveScoreboardApi.finishMatch(match.getId());
        Scoreboard scoreboard = liveScoreboardApi.createScoreboard();
        assertEquals(0, scoreboard.mathList().size());
        assertEquals("", scoreboard.getSummary());
    }

    @Test
    void givenThreeMatches_whenGetScoreboard_thenCorrectSummary() throws StartNewMatchException, InvalidMatchStateException, MatchNotFoundException {
        Match match1 = liveScoreboardApi.startNewMatch("Mexico", "Canada");
        liveScoreboardApi.updateMatch(match1.getId(), 0, 5);

        Match match2 = liveScoreboardApi.startNewMatch("Spain", "Brazil");
        liveScoreboardApi.updateMatch(match2.getId(), 10, 2);

        Match match3 = liveScoreboardApi.startNewMatch("Germany", "France");
        liveScoreboardApi.updateMatch(match3.getId(), 2, 2);

        Match match4 = liveScoreboardApi.startNewMatch("Uruguay", "Italy");
        liveScoreboardApi.updateMatch(match4.getId(), 6, 6);

        Match match5 = liveScoreboardApi.startNewMatch("Argentina", "Australia");
        liveScoreboardApi.updateMatch(match5.getId(), 3, 1);

        String expectedSummary = """
                1. Uruguay 6 - Italy 6
                2. Spain 10 - Brazil 2
                3. Mexico 0 - Canada 5
                4. Argentina 3 - Australia 1
                5. Germany 2 - France 2""";

        Scoreboard scoreboard = liveScoreboardApi.createScoreboard();

        assertEquals(expectedSummary, scoreboard.getSummary());
    }

    // end region
}