package live.match.service;

import live.match.api.InvalidMatchStateException;
import live.match.api.StartNewMatchException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

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
    void givenTeamsScoreForFinishedMatch_whenUpdateMatch_thenThrowInvalidMatchStateException() throws InvalidMatchStateException, StartNewMatchException {
        Match match = getStartedMatchBetweenHomeAndAway();
        match.finish();
        Exception exception = assertThrows(InvalidMatchStateException.class, () -> match.update(2, 0));
        assertNotNull(exception);
    }

    @Test
    void givenNotStartedMatchYet_whenUpdateMatch_thenThrowInvalidMatchStateException() {
        Match match = new Match("id", new Date(), new Team("Team1"), new Team("Team2"), matchService);
        Exception exception = assertThrows(InvalidMatchStateException.class, () -> match.update(1, 0));
        assertNotNull(exception);
    }

    @Test
    void givenTeamScoreLessThanZero_whenUpdateMatch_thenThrowInvalidMatchStateException() throws InvalidMatchStateException, StartNewMatchException {
        Match match = getStartedMatchBetweenHomeAndAway();
        match.update(2, 0);
        Exception exception = assertThrows(InvalidMatchStateException.class, () -> match.update(2, -1));
        assertNotNull(exception);
    }

    @Test
    void givenScoreLessThanCurrent_whenUpdateMatch_thenThrowInvalidMatchStateException() throws InvalidMatchStateException, StartNewMatchException {
        Match match = getStartedMatchBetweenHomeAndAway();
        match.update(2, 0);
        Exception exception = assertThrows(InvalidMatchStateException.class, () -> match.update(0, 1));
        assertNotNull(exception);
    }

    @Test
    void givenValidScores_whenUpdateMatch_thenNewScoresUpdated() throws InvalidMatchStateException, StartNewMatchException {
        Match match = getStartedMatchBetweenHomeAndAway();
        match.update(1, 0);
        assertEquals(1, match.getHomeTeamScore());
        assertEquals(0, match.getAwayTeamScore());
    }

    private Match getStartedMatchBetweenHomeAndAway() throws StartNewMatchException {
        return matchService.start("HomeTeam", "AwayTeam");
    }
}