package live.match.api;

import live.match.service.Match;
import live.match.service.Team;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LiveScoreboardApiImplTest {
    LiveScoreboardApi liveScoreboardApi;
    static Team homeTeam = new Team(UUID.randomUUID().toString(), "HomeTeam");
    static Team awayTeam = new Team(UUID.randomUUID().toString(), "AwayTeam");

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
        Match match = liveScoreboardApi.startNewMatch(homeTeam, awayTeam);
        assertNotNull(match);
        assertEquals(0, match.getScore());
        assertFalse(match.isFinished());
    }

    @Test
    void givenTeamHomeNull_whenStartNewMatch_thenThrowStartNewMatchException() {
        Exception exception = assertThrows(StartNewMatchException.class,
                                           () -> liveScoreboardApi.startNewMatch(null, awayTeam));

        assertNotNull(exception);
    }

    @Test
    void givenAwayTeamNull_whenStartNewMatch_thenThrowStartNewMatchException() {
        Exception exception = assertThrows(StartNewMatchException.class,
                                           () -> liveScoreboardApi.startNewMatch(homeTeam, null));
        assertNotNull(exception);
    }

    @Test
    void givenOccupiedHomeTeam_whenStartNewMatch_thenThrowStartNewMatchException() throws StartNewMatchException {
        Team snowTeam = new Team(UUID.randomUUID().toString(), "SnowTeam");
        liveScoreboardApi.startNewMatch(homeTeam, snowTeam);

        Exception exception = assertThrows(StartNewMatchException.class,
                                           () -> liveScoreboardApi.startNewMatch(homeTeam, awayTeam));
        assertNotNull(exception);
    }

    @Test
    void givenOccupiedAwayTeam_whenStartNewMatch_thenThrowStartNewMatchException() throws StartNewMatchException {
        Team snowTeam = new Team(UUID.randomUUID().toString(), "SnowTeam");
        liveScoreboardApi.startNewMatch(snowTeam, awayTeam);

        Exception exception = assertThrows(StartNewMatchException.class,
                                           () -> liveScoreboardApi.startNewMatch(homeTeam, awayTeam));
        assertNotNull(exception);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void givenHomeTeamNameBlank_whenStartNewMatch_thenThrowStartNewMatchException(String teamName) {
        Team blankNameTeam = new Team(UUID.randomUUID().toString(), teamName);
        Exception exception = assertThrows(StartNewMatchException.class,
                                           () -> liveScoreboardApi.startNewMatch(blankNameTeam, awayTeam));
        assertNotNull(exception);
    }
}