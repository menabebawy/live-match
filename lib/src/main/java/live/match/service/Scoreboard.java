package live.match.service;

import java.util.List;

public record Scoreboard(List<Match> matchList) {
    public String getSummary() {
        int index = 0;
        StringBuilder summeryBuilder = new StringBuilder();
        boolean newLine = false;
        for (Match match : matchList) {
            if (newLine) {
                summeryBuilder.append(System.lineSeparator());
            }
            newLine = true;
            summeryBuilder.append(++index).append(". ")
                    .append(match.getHomeTeam().name()).append(" ").append(match.getHomeTeamScore())
                    .append(" - ")
                    .append(match.getAwayTeam().name()).append(" ").append(match.getAwayTeamScore());
        }
        return summeryBuilder.toString();
    }
}