package live.match.service;

public final class Match {
    private final String id;
    private final long startedAt;
    private final Team homeTeam;
    private final Team awayTeam;
    private int homeTeamScore;
    private int awayTeamScore;
    private boolean finished;

    Match(String id, long startedAt, Team homeTeam, Team awayTeam) {
        this.id = id;
        this.startedAt = startedAt;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    public String getId() {
        return id;
    }

    long getStartedAt() {
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
}