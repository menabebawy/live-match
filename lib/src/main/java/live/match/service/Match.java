package live.match.service;

import live.match.api.InvalidMatchStateException;

public final class Match {
    private final String id;
    private final long startedAt;
    private final Team homeTeam;
    private final Team awayTeam;
    private int homeTeamScore;
    private int awayTeamScore;
    private boolean finished;

    private final MatchService matchService;

    Match(String id, long startedAt, Team homeTeam, Team awayTeam, MatchService matchService) {
        this.id = id;
        this.startedAt = startedAt;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchService = matchService;
    }

    public String getId() {
        return id;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public int getHomeTeamScore() {
        return homeTeamScore;
    }

    public int getAwayTeamScore() {
        return awayTeamScore;
    }

    public boolean isFinished() {
        return finished;
    }

    public int getScore() {
        return homeTeamScore + awayTeamScore;
    }

    void setTeamsScores(int homeTeamScore, int awayTeamScore) {
        this.homeTeamScore = homeTeamScore;
        this.awayTeamScore = awayTeamScore;
    }

    void setFinished() {
        this.finished = true;
    }

    public void finish() throws InvalidMatchStateException {
        matchService.finish(id);
    }
}
