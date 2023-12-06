package live.match.api;

import java.util.List;

public record Scoreboard(List<Math> mathList) {
    public String getSummary() {
        return "";
    }
}