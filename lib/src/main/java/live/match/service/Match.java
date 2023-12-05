package live.match.service;

import java.util.Date;

public final class Match {
    private final String id;
    private final Date startedAt;
    private final Team homeTeam;
    private final Team awayTeam;
    private int homeTeamScore;
    private int awayTeamScore;
    private boolean finished;

    Match(String id, Date startedAt, Team homeTeam, Team awayTeam) {
        this.id = id;
        this.startedAt = startedAt;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    public String getId() {
        return id;
    }

    public Date getStartedAt() {
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
}
