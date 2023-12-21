package live.match.api;

import live.match.service.InvalidMatchStateException;
import live.match.service.Match;
import live.match.service.MatchNotFoundException;
import live.match.service.MatchService;
import live.match.service.MockFactory;
import live.match.service.Scoreboard;
import live.match.service.StartNewMatchException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LiveScoreboardApiImplTest {
    LiveScoreboardApi liveScoreboardApi;
    static final String HOME_TEAM_NAME = "HomeTeam";
    static final String AWAY_TEAM_NAME = "AwayTeam";

    @Mock
    MatchService matchService;

    @BeforeEach
    void setUp() {
        liveScoreboardApi = new LiveScoreboardApiImpl(matchService);
    }

    @AfterEach
    void tearDown() {
        liveScoreboardApi = null;
    }

    @Test
    void givenValidTeamsNames_whenStartNewMatch_thenMatchStartedAndScoreZero() throws StartNewMatchException {
        Match mockMatch = MockFactory.createMatchInstance();

        when(matchService.start(HOME_TEAM_NAME, AWAY_TEAM_NAME)).thenReturn(mockMatch);

        Match match = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME);

        assertThat(match).isNotNull();
        assertThat(match.getId()).isEqualTo(mockMatch.getId());
        assertThat(match.getScore()).isEqualTo(mockMatch.getScore());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void givenTeamNameBlank_whenStartNewMatch_thenThrowIllegalArgumentException(String blankTeamName) {
        assertThatThrownBy(() -> liveScoreboardApi.startNewMatch(blankTeamName, AWAY_TEAM_NAME))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, blankTeamName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenTeamScoreLessThanZero_whenUpdateMatch_thenThrowIllegalArgumentException() throws StartNewMatchException {
        assertThatThrownBy(() -> liveScoreboardApi.updateMatch("id", 2, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -2, -5})
    void givenInvalidScore_whenUpdateMatch_thenThrowsIllegalArgumentException(int score) {
        assertThatThrownBy(() -> liveScoreboardApi.updateMatch("id", score, score))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void givenInvalidMatchId_whenUpdateMatch_thenThrowsIllegalArgumentException(String id) {
        assertThatThrownBy(() -> liveScoreboardApi.updateMatch(id, 1, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenValidScores_whenUpdateMatch_thenNewScoresUpdated() throws StartNewMatchException, MatchNotFoundException, InvalidMatchStateException {
        Match mockUpdatedMatch = MockFactory.updatedMatchInstance(1, 0);

        when(matchService.update(mockUpdatedMatch.getId(), 1, 0)).thenReturn(mockUpdatedMatch);

        Match updatedMatch = liveScoreboardApi.updateMatch(mockUpdatedMatch.getId(), 1, 0);

        assertThat(updatedMatch.getHomeTeamScore()).isEqualTo(mockUpdatedMatch.getHomeTeamScore());
        assertThat(updatedMatch.getAwayTeamScore()).isEqualTo(mockUpdatedMatch.getAwayTeamScore());
    }

    @Test
    void givenFinishedMatchId_whenFinishMatch_thenNothing() throws MatchNotFoundException {
        doNothing().when(matchService).finish("id");

        assertDoesNotThrow(() -> liveScoreboardApi.finishMatch("id"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void givenInvalidMatchId_whenFinishMatch_thenThrowsIllegalArgumentException(String id) {
        assertThatThrownBy(() -> liveScoreboardApi.finishMatch(id))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenNonMathsStarted_whenGetScoreboard_thenEmptySummary() {
        Scoreboard mockScoreboard = MockFactory.createEmptyScoreboardInstance();

        when(matchService.getSortedScoreboard()).thenReturn(mockScoreboard);

        Scoreboard scoreboard = liveScoreboardApi.getScoreboard();

        assertThat(scoreboard.getMatchList()).isEqualTo(mockScoreboard.getMatchList());
        assertThat(scoreboard.getSummary()).isEqualTo(mockScoreboard.getSummary());
    }

    @Test
    void givenOneMatch_whenGetScoreboard_thenSummaryOfOneMatch() {
        Scoreboard mockScoreboard = MockFactory.createOneMatchScoreboardInstance();

        when(matchService.getSortedScoreboard()).thenReturn(mockScoreboard);

        Scoreboard scoreboard = liveScoreboardApi.getScoreboard();

        assertThat(scoreboard.getMatchList()).isEqualTo(mockScoreboard.getMatchList());
        assertThat(scoreboard.getSummary()).isEqualTo(mockScoreboard.getSummary());
    }

}