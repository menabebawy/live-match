package live.match.service;

import live.match.api.InvalidMatchStateException;
import live.match.api.MatchNotFoundException;
import live.match.api.StartNewMatchException;
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
    void givenTeamsScoreForFinishedMatch_whenUpdateMatch_thenThrowInvalidMatchStateException() throws InvalidMatchStateException, StartNewMatchException, MatchNotFoundException {
        Match match = getStartedMatchBetweenHomeAndAway();
        match.finish();
        Exception exception = assertThrows(InvalidMatchStateException.class,
                                           () -> matchService.update(match.getId(), 2, 0));
        assertNotNull(exception);
    }

    @Test
    void givenNotStartedMatchYet_whenUpdateMatch_thenThrowMatchNotFoundException() {
        Match match = new Match("id", System.nanoTime(), new Team("Team1"), new Team("Team2"), matchService);
        Exception exception = assertThrows(MatchNotFoundException.class,
                                           () -> matchService.update(match.getId(), 1, 0));
        assertNotNull(exception);
    }

    @Test
    void givenTeamScoreLessThanZero_whenUpdateMatch_thenThrowInvalidMatchStateException() throws InvalidMatchStateException, StartNewMatchException, MatchNotFoundException {
        Match match = getStartedMatchBetweenHomeAndAway();
        matchService.update(match.getId(), 2, 0);
        Exception exception = assertThrows(InvalidMatchStateException.class,
                                           () -> matchService.update(match.getId(), 2, -1));
        assertNotNull(exception);
    }

    @Test
    void givenScoreLessThanCurrent_whenUpdateMatch_thenThrowInvalidMatchStateException() throws InvalidMatchStateException, StartNewMatchException, MatchNotFoundException {
        Match match = getStartedMatchBetweenHomeAndAway();
        matchService.update(match.getId(), 2, 0);
        Exception exception = assertThrows(InvalidMatchStateException.class,
                                           () -> matchService.update(match.getId(), 0, 1));
        assertNotNull(exception);
    }

    @Test
    void givenValidScores_whenUpdateMatch_thenNewScoresUpdated() throws InvalidMatchStateException, StartNewMatchException, MatchNotFoundException {
        Match match = getStartedMatchBetweenHomeAndAway();
        Match updatedMatch = matchService.update(match.getId(), 1, 0);
        assertEquals(1, updatedMatch.getHomeTeamScore());
        assertEquals(0, updatedMatch.getAwayTeamScore());
    }

    @Test
    void givenFreshStartedMatch_thenAllFieldAreDefault() throws StartNewMatchException {
        Match match = getStartedMatchBetweenHomeAndAway();
        assertEquals(0, match.getHomeTeamScore());
        assertEquals(0, match.getAwayTeamScore());
        assertEquals(0, match.getScore());
        assertFalse(match.isFinished());
    }

    @Test
    void givenFinishedMatchId_whenFinishMatch_thenInvalidMatchStateException() throws StartNewMatchException, InvalidMatchStateException, MatchNotFoundException {
        Match match = getStartedMatchBetweenHomeAndAway();
        matchService.finish(match.getId());
        Exception exception = assertThrows(InvalidMatchStateException.class, () -> matchService.finish(match.getId()));
        assertNotNull(exception);
    }

    @Test
    void givenNotFoundMatchId_whenFinishMatch_thenMatchNotFoundException() {
        Exception exception = assertThrows(MatchNotFoundException.class, () -> matchService.finish("id"));
        assertNotNull(exception);
    }

    @Test
    void givenInProgressMatchId_whenFinishMatch_thenMatchFinisher() throws StartNewMatchException, InvalidMatchStateException, MatchNotFoundException {
        Match match = getStartedMatchBetweenHomeAndAway();
        Match finishedMatch = matchService.finish(match.getId());
        assertTrue(finishedMatch.isFinished());
    }

    private Match getStartedMatchBetweenHomeAndAway() throws StartNewMatchException {
        return matchService.start("HomeTeam", "AwayTeam");
    }
}