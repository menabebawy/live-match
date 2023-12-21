package live.match.api;

import live.match.service.InvalidMatchStateException;
import live.match.service.Match;
import live.match.service.MatchNotFoundException;
import live.match.service.Scoreboard;
import live.match.service.StartNewMatchException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LiveScoreBoardApiIntegrationTest {
    LiveScoreboardApi liveScoreboardApi;

    @BeforeEach
    void setUp() {
        liveScoreboardApi = LiveScoreboardApi.createInstance();
    }

    @AfterEach
    void tearDown() {
        liveScoreboardApi = null;
    }

    @Test
    void shouldStartNewMatch() throws StartNewMatchException {
        Match match = liveScoreboardApi.startNewMatch("Canada", "Italy");

        assertThat(match.getScore()).isZero();
        assertThat(match.getHomeTeam().name()).isEqualTo("Canada");
        assertThat(match.getAwayTeam().name()).isEqualTo("Italy");
    }

    @Test
    void shouldStartNewMatchIfTeamsAreCurrentlyFree() throws StartNewMatchException, MatchNotFoundException {
        Match matchCanadaParis = liveScoreboardApi.startNewMatch("Canada", "France");
        liveScoreboardApi.finishMatch(matchCanadaParis.getId());

        Match matchCanadaItaly = liveScoreboardApi.startNewMatch("Canada", "Italy");
        assertThat(matchCanadaItaly).isNotNull();
        assertThat(matchCanadaItaly.getScore()).isZero();

        Match matchParisVienna = liveScoreboardApi.startNewMatch("France", "Austria");
        assertThat(matchParisVienna).isNotNull();
        assertThat(matchParisVienna.getScore()).isZero();
    }

    @Test
    void shouldNotStartNewMatchIfTeamIsOccupied() throws StartNewMatchException {
        liveScoreboardApi.startNewMatch("Canada", "France");

        assertThatThrownBy(() -> liveScoreboardApi.startNewMatch("Austria", "France"))
                .isInstanceOf(StartNewMatchException.class);
    }

    @Test
    void shouldUpdateInProgressMatch() throws StartNewMatchException, InvalidMatchStateException, MatchNotFoundException {
        Match match = liveScoreboardApi.startNewMatch("Germany", "Italy");

        liveScoreboardApi.updateMatch(match.getId(), 1, 0);
        liveScoreboardApi.updateMatch(match.getId(), 1, 1);
        liveScoreboardApi.updateMatch(match.getId(), 2, 1);

        assertThat(match.getScore()).isEqualTo(3);
        assertThat(match.getHomeTeamScore()).isEqualTo(2);
        assertThat(match.getAwayTeamScore()).isEqualTo(1);

    }

    @Test
    void shouldNotUpdateFinishedMatch() throws StartNewMatchException, MatchNotFoundException {
        Match match = liveScoreboardApi.startNewMatch("Germany", "Italy");

        liveScoreboardApi.finishMatch(match.getId());

        assertThatThrownBy(() -> liveScoreboardApi.updateMatch(match.getId(), 1, 1))
                .isInstanceOf(MatchNotFoundException.class);
    }

    @Test
    void shouldFinishInProgressMatch() throws StartNewMatchException, InvalidMatchStateException, MatchNotFoundException {
        Match match = liveScoreboardApi.startNewMatch("Germany", "Italy");

        liveScoreboardApi.updateMatch(match.getId(), 1, 0);
        liveScoreboardApi.updateMatch(match.getId(), 2, 0);

        assertDoesNotThrow(() -> liveScoreboardApi.finishMatch(match.getId()));
    }

    @Test
    void shouldEmptyScoreboardWhenNotInProgressMatches() throws StartNewMatchException, MatchNotFoundException {
        Match match = liveScoreboardApi.startNewMatch("Germany", "Italy");
        liveScoreboardApi.finishMatch(match.getId());

        Scoreboard scoreboard = liveScoreboardApi.getScoreboard();

        assertThat(scoreboard.getMatchList()).isEmpty();
        assertThat(scoreboard.getSummary()).isEmpty();
    }

    @Test
    void shouldScoreboardShowsAllInProgressMatches() throws StartNewMatchException, InvalidMatchStateException, MatchNotFoundException {
        String expectedSummary = """
                1. Uruguay 6 - Italy 6
                2. Spain 10 - Brazil 2
                3. Mexico 0 - Canada 5
                4. Argentina 3 - Australia 1
                5. Germany 2 - France 2""";

        Match matchMexicoCanada = liveScoreboardApi.startNewMatch("Mexico", "Canada");
        liveScoreboardApi.updateMatch(matchMexicoCanada.getId(), 0, 5);

        Match matchSpainBrazil = liveScoreboardApi.startNewMatch("Spain", "Brazil");
        liveScoreboardApi.updateMatch(matchSpainBrazil.getId(), 10, 2);

        Match matchGermanyFrance = liveScoreboardApi.startNewMatch("Germany", "France");
        liveScoreboardApi.updateMatch(matchGermanyFrance.getId(), 2, 2);

        Match matchUruguayItaly = liveScoreboardApi.startNewMatch("Uruguay", "Italy");
        liveScoreboardApi.updateMatch(matchUruguayItaly.getId(), 6, 6);

        Match matchArgentinaAustralia = liveScoreboardApi.startNewMatch("Argentina", "Australia");
        liveScoreboardApi.updateMatch(matchArgentinaAustralia.getId(), 3, 1);

        Scoreboard scoreboard = liveScoreboardApi.getScoreboard();

        assertThat(scoreboard.getSummary()).isEqualTo(expectedSummary);
    }
}
