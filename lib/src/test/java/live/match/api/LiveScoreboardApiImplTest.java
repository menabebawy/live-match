package live.match.api;

import live.match.service.InvalidMatchStateException;
import live.match.service.Match;
import live.match.service.MatchNotFoundException;
import live.match.service.Scoreboard;
import live.match.service.StartNewMatchException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LiveScoreboardApiImplTest {
    LiveScoreboardApi liveScoreboardApi;
    static final String HOME_TEAM_NAME = "HomeTeam";
    static final String AWAY_TEAM_NAME = "AwayTeam";
    static final String SNOW_TEAM_NAME = "SnowTeam";

    @BeforeEach
    void setUp() {
        liveScoreboardApi = LiveScoreboardApi.createInstance();
    }

    @AfterEach
    void tearDown() {
        liveScoreboardApi = null;
    }

    // region start new match
    @Test
    void givenValidTeamsNames_whenStartNewMatch_thenMatchStartedAndScoreZero() throws StartNewMatchException {
        Match match = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME);
        assertThat(match).isNotNull();
        assertThat(match.getScore()).isZero();
    }

    @Test
    void givenJustFreeTeams_whenStartNewMatch_thenMatchStarted() throws StartNewMatchException, MatchNotFoundException {
        Match firstMatch = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME);
        liveScoreboardApi.finishMatch(firstMatch.getId());

        Match secondMatch = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, "Paris");
        assertThat(secondMatch.getScore()).isZero();

        Match thirdMatch = liveScoreboardApi.startNewMatch("Vienna", AWAY_TEAM_NAME);
        assertThat(thirdMatch.getScore()).isZero();
    }

    @Test
    void givenTeamsNamesSurroundWhitespace_whenStartNewMatch_thenTrimNames() throws StartNewMatchException {
        Match match = liveScoreboardApi.startNewMatch(" " + HOME_TEAM_NAME + "   ", AWAY_TEAM_NAME);
        assertThat(match.getHomeTeam().name()).isEqualTo(HOME_TEAM_NAME);
    }

    @Test
    void givenOccupiedHomeTeam_whenStartNewMatch_thenThrowStartNewMatchException() throws StartNewMatchException {
        liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, SNOW_TEAM_NAME);

        assertThatThrownBy(() -> liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, "Paris"))
                .isInstanceOf(StartNewMatchException.class);
    }

    @Test
    void givenOccupiedAwayTeam_whenStartNewMatch_thenThrowStartNewMatchException() throws StartNewMatchException {
        liveScoreboardApi.startNewMatch(SNOW_TEAM_NAME, AWAY_TEAM_NAME);

        assertThatThrownBy(() -> liveScoreboardApi.startNewMatch("Paris", AWAY_TEAM_NAME))
                .isInstanceOf(StartNewMatchException.class);
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

    // endregion

    // region update match

    @Test
    void givenTeamsScoreForFinishedMatch_whenUpdateMatch_thenThrowsInvalidMatchStateException() throws StartNewMatchException, MatchNotFoundException {
        Match match = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME);
        liveScoreboardApi.finishMatch(match.getId());

        assertThatThrownBy(() -> liveScoreboardApi.updateMatch(match.getId(), 0, 1))
                .isInstanceOf(MatchNotFoundException.class);
    }

    @Test
    void givenNotStartedMatchYet_whenUpdateMatch_thenThrowMatchNotFoundException() {
        assertThatThrownBy(() -> liveScoreboardApi.updateMatch("id", 1, 0))
                .isInstanceOf(MatchNotFoundException.class);
    }

    @Test
    void givenTeamScoreLessThanZero_whenUpdateMatch_thenThrowIllegalArgumentException() throws StartNewMatchException {
        Match match = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME);
        String id = match.getId();
        assertThatThrownBy(() -> liveScoreboardApi.updateMatch(id, 2, -1))
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
    void givenScoreLessThanCurrent_whenUpdateMatch_thenThrowsInvalidMatchStateException() throws StartNewMatchException, MatchNotFoundException, InvalidMatchStateException {
        Match match = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME);
        liveScoreboardApi.updateMatch(match.getId(), 2, 1);

        assertThatThrownBy(() -> liveScoreboardApi.updateMatch(match.getId(), 0, 1))
                .isInstanceOf(InvalidMatchStateException.class);

        assertThatThrownBy(() -> liveScoreboardApi.updateMatch(match.getId(), 1, 1))
                .isInstanceOf(InvalidMatchStateException.class);
    }

    @Test
    void givenValidScores_whenUpdateMatch_thenNewScoresUpdated() throws StartNewMatchException, MatchNotFoundException, InvalidMatchStateException {
        Match match = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME);
        Match updatedMatch = liveScoreboardApi.updateMatch(match.getId(), 1, 0);
        assertThat(updatedMatch.getHomeTeamScore()).isEqualTo(1);
        assertThat(updatedMatch.getAwayTeamScore()).isZero();
    }

    // endregion

    // region finish match

    @Test
    void givenFinishedMatchId_whenFinishMatch_thenNothing() throws StartNewMatchException {
        Match match = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME);
        assertDoesNotThrow(() -> liveScoreboardApi.finishMatch(match.getId()));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void givenInvalidMatchId_whenFinishMatch_thenThrowsIllegalArgumentException(String id) {
        assertThatThrownBy(() -> liveScoreboardApi.finishMatch(id))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenNotFoundMatchId_whenFinishMatch_thenThrowsMatchNotFoundException() {
        assertThatThrownBy(() -> liveScoreboardApi.finishMatch("id"))
                .isInstanceOf(MatchNotFoundException.class);
    }

    // endregion

    // region get scoreboard

    @Test
    void givenNonMathsStarted_whenGetScoreboard_thenEmptySummary() {
        Scoreboard scoreboard = liveScoreboardApi.getScoreboard();
        assertThat(scoreboard.getMatchList()).isEmpty();
        assertThat(scoreboard.getSummary()).isEmpty();
    }

    @Test
    void givenOneMatch_whenGetScoreboard_thenSummaryOfOneMatch() throws StartNewMatchException, MatchNotFoundException, InvalidMatchStateException {
        Match match = liveScoreboardApi.startNewMatch("Mexico", "Canada");
        liveScoreboardApi.updateMatch(match.getId(), 1, 0);
        liveScoreboardApi.updateMatch(match.getId(), 1, 1);
        liveScoreboardApi.updateMatch(match.getId(), 2, 1);
        liveScoreboardApi.updateMatch(match.getId(), 3, 1);

        String expectedSummary = "1. Mexico 3 - Canada 1";

        Scoreboard scoreboard = liveScoreboardApi.getScoreboard();

        assertThat(scoreboard.getSummary()).isEqualTo(expectedSummary);
    }

    @Test
    void givenOneFinishedMatch_whenGetScoreboard_thenEmptySummary() throws StartNewMatchException, MatchNotFoundException {
        Match match = liveScoreboardApi.startNewMatch(HOME_TEAM_NAME, AWAY_TEAM_NAME);

        Scoreboard oneMatchScoreboard = liveScoreboardApi.getScoreboard();
        assertThat(oneMatchScoreboard.getMatchList()).hasSize(1);
        assertThat(oneMatchScoreboard.getSummary()).contains(HOME_TEAM_NAME);

        liveScoreboardApi.finishMatch(match.getId());

        Scoreboard mostUpdatedScoreboard = liveScoreboardApi.getScoreboard();
        assertThat(mostUpdatedScoreboard.getMatchList()).isEmpty();
        assertThat(mostUpdatedScoreboard.getSummary()).isEmpty();
    }

    @Test
    void givenThreeMatches_whenGetScoreboard_thenCorrectSummary() throws StartNewMatchException, MatchNotFoundException, InvalidMatchStateException {
        Match match1 = liveScoreboardApi.startNewMatch("Mexico", "Canada");
        liveScoreboardApi.updateMatch(match1.getId(), 0, 5);

        Match match2 = liveScoreboardApi.startNewMatch("Spain", "Brazil");
        liveScoreboardApi.updateMatch(match2.getId(), 10, 2);

        Match match3 = liveScoreboardApi.startNewMatch("Germany", "France");
        liveScoreboardApi.updateMatch(match3.getId(), 2, 2);

        Match match4 = liveScoreboardApi.startNewMatch("Uruguay", "Italy");
        liveScoreboardApi.updateMatch(match4.getId(), 6, 6);

        Match match5 = liveScoreboardApi.startNewMatch("Argentina", "Australia");
        liveScoreboardApi.updateMatch(match5.getId(), 3, 1);

        String expectedSummary = """
                1. Uruguay 6 - Italy 6
                2. Spain 10 - Brazil 2
                3. Mexico 0 - Canada 5
                4. Argentina 3 - Australia 1
                5. Germany 2 - France 2""";

        Scoreboard scoreboard = liveScoreboardApi.getScoreboard();

        assertThat(scoreboard.getSummary()).isEqualTo(expectedSummary);
    }

    // end region
}