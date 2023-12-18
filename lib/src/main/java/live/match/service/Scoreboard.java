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
            summeryBuilder.append(generateSummeryRow(++index, match));
        }
        return summeryBuilder.toString();
    }

    private static String generateSummeryRow(int index, Match match) {
        return index + ". " +
                match.getHomeTeam().name() + " " + match.getHomeTeamScore() +
                " - " +
                match.getAwayTeam().name() + " " + match.getAwayTeamScore();
    }
}