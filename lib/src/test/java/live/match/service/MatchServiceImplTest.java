package live.match.service;

import live.match.api.InvalidMatchStateException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchServiceImplTest {
    MatchService matchService;

    @BeforeEach
    void setUp() {
        matchService = MatchService.createInstant();
    }

    @AfterEach
    void tearDown() {
        matchService = null;
    }

    @Test
    void givenTeamsScoreForFinishedMatch_whenUpdateMatch_thenThrowInvalidMatchStateException() throws InvalidMatchStateException {
        Match match = MockMatch.createStartedMatchInstant();
        match.finish();
        Exception exception = assertThrows(InvalidMatchStateException.class, () -> match.update(2, 0));
        assertNotNull(exception);
    }

    @Test
    void givenTeamScoreLessThanZero_whenUpdateMatch_thenThrowInvalidMatchStateException() throws InvalidMatchStateException {
        Match match = MockMatch.createStartedMatchInstant();
        match.update(2, 0);
        Exception exception = assertThrows(InvalidMatchStateException.class, () -> match.update(2, -1));
        assertNotNull(exception);
    }

    @Test
    void givenScoreLessThanCurrent_whenUpdateMatch_thenThrowInvalidMatchStateException() throws InvalidMatchStateException {
        Match match = MockMatch.createStartedMatchInstant();
        match.update(2, 0);
        Exception exception = assertThrows(InvalidMatchStateException.class, () -> match.update(0, 1));
        assertNotNull(exception);
    }

    @Test
    void givenValidScores_whenUpdateMatch_thenNewScoresUpdated() throws InvalidMatchStateException {
        Match match = MockMatch.createStartedMatchInstant();
        match.update(1, 0);
        assertEquals(1, match.getHomeTeamScore());
        assertEquals(0, match.getAwayTeamScore());
    }
}