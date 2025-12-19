import java.io.Serializable;
import java.util.Arrays;

public class PlayerProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;

    // Overall stats (human-vs-human OR anything you decide counts as overall)
    private int wins;
    private int losses;
    private int draws;

    // AI stats (separate)
    private int aiWins;
    private int aiLosses;
    private int aiDraws;

    // Rolling last 10 results: 1 = win, 0 = draw, -1 = loss, 2 = empty
    private final int[] last10 = new int[10];
    private int last10Index = 0;
    private int last10Count = 0;

    public PlayerProfile(String name) {
        this.name = name;
        Arrays.fill(last10, 2);
    }

    public String getName() { return name; }

    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getDraws() { return draws; }

    public int getAiWins() { return aiWins; }
    public int getAiLosses() { return aiLosses; }
    public int getAiDraws() { return aiDraws; }

    // result: 1 win, 0 draw, -1 loss
    public void recordOverallResult(int result) {
        if (result == 1) wins++;
        else if (result == -1) losses++;
        else draws++;
        addToLast10(result);
    }

    // result: 1 win, 0 draw, -1 loss (from the HUMAN player's perspective)
    public void recordAiResult(int result) {
        if (result == 1) aiWins++;
        else if (result == -1) aiLosses++;
        else aiDraws++;
    }

    private void addToLast10(int result) {
        last10[last10Index] = result;
        last10Index = (last10Index + 1) % last10.length;
        if (last10Count < last10.length) last10Count++;
    }

    public String last10AsString() {
        // Print oldest -> newest
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int start = (last10Index - last10Count);
        while (start < 0) start += last10.length;

        for (int i = 0; i < last10Count; i++) {
            int idx = (start + i) % last10.length;
            int v = last10[idx];
            char c;
            if (v == 1) c = 'W';
            else if (v == 0) c = 'D';
            else if (v == -1) c = 'L';
            else c = '-';
            sb.append(c);
            if (i != last10Count - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    public String prettyProfile() {
        StringBuilder sb = new StringBuilder();
        sb.append("Player: ").append(name).append("\n");
        sb.append("Overall W/D/L: ").append(wins).append("/")
          .append(draws).append("/").append(losses).append("\n");
        sb.append("AI      W/D/L: ").append(aiWins).append("/")
          .append(aiDraws).append("/").append(aiLosses).append("\n");
        sb.append("Last 10: ").append(last10AsString()).append("\n");
        return sb.toString();
    }
}
