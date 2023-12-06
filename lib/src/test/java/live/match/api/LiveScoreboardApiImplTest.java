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
}