import java.util.ArrayList;
import java.util.List;

public class MonteCarloNode {

    int move;    // column number
    int wins;
    int simulations;
    List<MonteCarloNode> children;

    public MonteCarloNode(int move) {
        this.move = move;
        this.wins = 0;
        this.simulations = 0;
        this.children = new ArrayList<>();
    }

    public void recordResult(boolean win) {
        simulations++;
        if (win) {
            wins++;
        }
    }

    public double winRate() {
        if (simulations == 0) {
            return 0.0;
        }
        return (double) wins / simulations;
    }
}
