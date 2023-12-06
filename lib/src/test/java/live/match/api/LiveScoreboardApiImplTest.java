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
    void givenOneMatch_whenGetScoreboard_thenSummaryOfOneMatch() throws StartNewMatchException, InvalidMatchStateException {
        Match match = liveScoreboardApi.startNewMatch("Mexico", "Canada");
        match.update(1, 0);
        match.update(1, 1);
        match.update(2, 1);
        match.update(3, 1);

        String expectedSummary = "1. Mexico 3 - Canada 1";

        Scoreboard scoreboard = liveScoreboardApi.createScoreboard();

        assertEquals(expectedSummary, scoreboard.getSummary());
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
        Match match1 = liveScoreboardApi.startNewMatch("Mexico", "Canada");
        match1.update(0, 5);

        Match match2 = liveScoreboardApi.startNewMatch("Spain", "Brazil");
        match2.update(10, 2);
        System.out.println(match2.getStartedAt());

        Match match3 = liveScoreboardApi.startNewMatch("Germany", "France");
        match3.update(2, 2);

        Match match4 = liveScoreboardApi.startNewMatch("Uruguay", "Italy");
        match4.update(6, 6);
        System.out.println(match4.getStartedAt());

        Match match5 = liveScoreboardApi.startNewMatch("Argentina", "Australia");
        match5.update(3, 1);

        String expectedSummary = """
                1. Uruguay 6 - Italy 6
                2. Spain 10 - Brazil 2
                3. Mexico 0 - Canada 5
                4. Argentina 3 - Australia 1
                5. Germany 2 - France 2""";

        Scoreboard scoreboard = liveScoreboardApi.createScoreboard();

        assertEquals(expectedSummary, scoreboard.getSummary());
    }
}