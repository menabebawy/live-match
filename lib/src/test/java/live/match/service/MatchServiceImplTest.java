package live.match.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MatchServiceImplTest {
    MatchService matchService;

    @BeforeEach
    void setUp() {
        matchService = MatchService.createInstance();
    }

    @AfterEach
    void tearDown() {
        matchService = null;
    }

    // region start match

    @Test
    void givenFreshStartedMatch_thenAllFieldAreDefault() throws StartNewMatchException {
        Match match = getStartedMatchBetweenHomeAndAway();
        assertThat(match.getHomeTeamScore()).isZero();
        assertThat(match.getAwayTeamScore()).isZero();
        assertThat(match.getScore()).isZero();
    }

    // endregion

    // region update match
    @Test
    void givenTeamsScoreForFinishedMatch_whenUpdateMatch_thenThrowsInvalidMatchStateException() throws StartNewMatchException, MatchNotFoundException {
        Match match = getStartedMatchBetweenHomeAndAway();
        matchService.finish(match.getId());

        assertThatThrownBy(() -> matchService.update(match.getId(), 2, 0))
                .isInstanceOf(MatchNotFoundException.class);
    }

    @Test
    void givenNotStartedMatchYet_whenUpdateMatch_thenThrowsMatchNotFoundException() {
        Match match = new Match("id", System.nanoTime(), new Team("Team1"), new Team("Team2"));
        assertThatThrownBy(() -> matchService.update(match.getId(), 1, 0))
                .isInstanceOf(MatchNotFoundException.class);
    }

    @Test
    void givenScoreLessThanCurrent_whenUpdateMatch_thenThrowsInvalidMatchStateException() throws StartNewMatchException, MatchNotFoundException, InvalidMatchStateException {
        Match match = getStartedMatchBetweenHomeAndAway();
        matchService.update(match.getId(), 2, 0);

        assertThatThrownBy(() -> matchService.update(match.getId(), 0, 1))
                .isInstanceOf(InvalidMatchStateException.class);
    }

    @Test
    void givenValidScores_whenUpdateMatch_thenNewScoresUpdated() throws StartNewMatchException, MatchNotFoundException, InvalidMatchStateException {
        Match match = getStartedMatchBetweenHomeAndAway();
        Match updatedMatch = matchService.update(match.getId(), 1, 0);
        assertThat(updatedMatch.getHomeTeamScore()).isEqualTo(1);
        assertThat(updatedMatch.getAwayTeamScore()).isZero();
    }

    // endregion

    // region finish match

    @Test
    void givenNotFoundMatchId_whenFinishMatch_thenThrowsMatchNotFoundException() {
        assertThatThrownBy(() -> matchService.finish("id"))
                .isInstanceOf(MatchNotFoundException.class);
    }

    @Test
    void givenInProgressMatchId_whenFinishMatch_thenMatchFinisher() throws StartNewMatchException {
        Match match = getStartedMatchBetweenHomeAndAway();
        assertDoesNotThrow(() -> matchService.finish(match.getId()));
    }

    private Match getStartedMatchBetweenHomeAndAway() throws StartNewMatchException {
        return matchService.start("HomeTeam", "AwayTeam");
    }

    // endregion
}