package live.match.service;

import live.match.api.InvalidMatchStateException;

import java.util.Date;

public final class Match {
    private final String id;
    private final Date startedAt;
    private final Team homeTeam;
    private final Team awayTeam;
    private int homeTeamScore;
    private int awayTeamScore;
    private boolean finished;

    private final MatchService matchService;

    Match(String id, Date startedAt, Team homeTeam, Team awayTeam, MatchService matchService) {
        this.id = id;
        this.startedAt = startedAt;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchService = matchService;
    }

    public String getId() {
        return id;
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

    public void update(int homeTeamScore, int awayTeamScore) throws InvalidMatchStateException {
        if (homeTeamScore < 0 || awayTeamScore < 0) {
            throw new InvalidMatchStateException("Score less than 0 is not allowed");
        }
        matchService.update(id, homeTeamScore, awayTeamScore);
        setTeamsScores(homeTeamScore, awayTeamScore);
    }

    public void finish() throws InvalidMatchStateException {
        matchService.finish(id);
    }
}
