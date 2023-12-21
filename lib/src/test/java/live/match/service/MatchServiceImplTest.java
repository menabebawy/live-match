package live.match.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchServiceImplTest {
    MatchService matchService;

    @Mock
    Scoreboard scoreboard;

    @BeforeEach
    void setUp() {
        matchService = new MatchServiceImpl(scoreboard);
    }

    @AfterEach
    void tearDown() {
        matchService = null;
    }

    @Test
    void givenFreshStartedMatch_thenAllFieldAreDefault() throws StartNewMatchException {
        Match mockMatch = MockFactory.createMatchInstance();

        when(scoreboard.addMatch(any())).thenReturn(mockMatch);

        Match match = matchService.start("HomeTeam", "AwayTeam");

        assertThat(match.getHomeTeamScore()).isZero();
        assertThat(match.getAwayTeamScore()).isZero();
        assertThat(match.getScore()).isZero();
        assertThat(match.getHomeTeam().name()).isEqualTo(mockMatch.getHomeTeam().name());
        assertThat(match.getAwayTeam().name()).isEqualTo(mockMatch.getAwayTeam().name());
    }

    @Test
    void givenScoreLessThanCurrent_whenUpdateMatch_thenThrowsInvalidMatchStateException() {
        Match currentMatch = MockFactory.updatedMatchInstance(2, 1);

        when(scoreboard.getOptionalMatch(any())).thenReturn(Optional.of(currentMatch));

        assertThatThrownBy(() -> matchService.update("id", 2, 0))
                .isInstanceOf(InvalidMatchStateException.class);
    }

    @Test
    void givenValidScores_whenUpdateMatch_thenUpdateMatch() throws InvalidMatchStateException, MatchNotFoundException {
        Match currentMatch = MockFactory.updatedMatchInstance(2, 1);

        when(scoreboard.getOptionalMatch(any())).thenReturn(Optional.of(currentMatch));

        Match updatedMatch = matchService.update("id", 2, 2);

        assertThat(updatedMatch.getScore()).isEqualTo(4);
        assertThat(updatedMatch.getAwayTeamScore()).isEqualTo(2);
        assertThat(currentMatch.getHomeTeamScore()).isEqualTo(2);
    }

    @Test
    void givenNotFoundMatchId_whenUpdateMatch_thenThrowsMatchNotFoundException() {
        when(scoreboard.getOptionalMatch(any())).thenReturn(empty());

        assertThatThrownBy(() -> matchService.update("id", 2, 1))
                .isInstanceOf(MatchNotFoundException.class);
    }

    @Test
    void givenNotFoundMatchId_whenFinishMatch_thenThrowsMatchNotFoundException() {
        when(scoreboard.getOptionalMatch(any())).thenReturn(empty());

        assertThatThrownBy(() -> matchService.finish("id")).isInstanceOf(MatchNotFoundException.class);
    }

    @Test
    void givenInProgressMatchId_whenFinishMatch_thenMatchRemoved() {
        Match currentMatch = MockFactory.updatedMatchInstance(2, 1);

        when(scoreboard.getOptionalMatch("id")).thenReturn(Optional.of(currentMatch));

        assertDoesNotThrow(() -> matchService.finish("id"));
    }

}