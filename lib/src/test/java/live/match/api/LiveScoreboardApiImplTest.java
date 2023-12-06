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

    @Test
    void givenNoNullTeams_whenStartNewMatch_thenMatchStartedInProgressAndScoreZero() throws StartNewMatchException {
        Match match = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME);
        assertNotNull(match);
        assertEquals(0, match.getScore());
        assertFalse(match.isFinished());
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

    @Test
    void givenNonMathsStarted_whenGetScoreboard_thenEmptySummary() {
        Scoreboard scoreboard = liveScoreboardApi.createScoreboard();
        assertEquals(0, scoreboard.mathList().size());
        assertEquals("", scoreboard.getSummary());
    }

    @Test
    void givenOneMatch_whenGetScoreboard_thenSummaryOfOneMatch() throws StartNewMatchException {
        Match match = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME);
        Scoreboard scoreboard = liveScoreboardApi.createScoreboard();
        assertEquals(1, scoreboard.mathList().size());
        assertTrue(scoreboard.getSummary().contains(HOME_TEAM_NAME));
        assertTrue(scoreboard.getSummary().contains(AWAY_TEAM_NAME));
    }

    @Test
    void givenOneFinishedMatch_whenGetScoreboard_thenEmptySummary() throws InvalidMatchStateException, StartNewMatchException {
        Match match = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME);
        match.finish();
        Scoreboard scoreboard = liveScoreboardApi.createScoreboard();
        assertEquals(0, scoreboard.mathList().size());
        assertEquals("", scoreboard.getSummary());
    }

    @Test
    void givenThreeMatches_whenGetScoreboard_thenCorrectSummary() throws StartNewMatchException, InvalidMatchStateException {
        Match match1 = liveScoreboardApi.startNewMatch("Home1", "Away1");
        match1.update(3, 2);

        Match match2 = liveScoreboardApi.startNewMatch("Home2", "Away2");
        match1.update(1, 1);

        Match match3 = liveScoreboardApi.startNewMatch("Home3", "Away3");
        match1.update(1, 1);

        String expectedSummary = "build.....";

        Scoreboard scoreboard = liveScoreboardApi.createScoreboard();
        assertEquals(3, scoreboard.mathList().size());
        assertEquals(expectedSummary, scoreboard.getSummary());
    }

    @Test
    void givenThreeMatches_twoHasSameScore_whenGetScoreboard_thenCorrectSummary() throws StartNewMatchException, InvalidMatchStateException {
        Match match1 = liveScoreboardApi.startNewMatch("Home1", "Away1");
        match1.update(3, 2);

        Match match2 = liveScoreboardApi.startNewMatch("Home2", "Away2");
        match1.update(1, 1);

        Match match3 = liveScoreboardApi.startNewMatch("Home3", "Away3");
        match1.update(2, 3);

        String expectedSummary = "build.....";

        Scoreboard scoreboard = liveScoreboardApi.createScoreboard();
        assertEquals(3, scoreboard.mathList().size());
        assertEquals(expectedSummary, scoreboard.getSummary());
    }
}