public class Tournament {
    
    private int id;
    private TournamentQueue matchQueue;
    private AVLTree standings;

    public Tournament(int id, Player[] players) {
        this.id = id;
        this.matchQueue = new TournamentQueue(players);
        this.standings = new AVLTree();
        for (Player player : players) {
            standings.insert(player, 0); // Initialize with 0 wins
        }
    }

    public int getId() {
        return id;
    }

    public Match getNextMatch() {
        return matchQueue.peek();
    }

    public Match playNextMatch() {
        return matchQueue.dequeue();
    }

    public boolean hasMoreMatches() {
        return !matchQueue.isEmpty();
    }

    public void addWin(Player player) {
        int wins = standings.remove(player);
        if (wins != Integer.MIN_VALUE) { // player exists
            standings.insert(player, wins + 1);
        }
    }

    public void addLoss(Player player) {
        int wins = standings.remove(player);
        if (wins != Integer.MIN_VALUE) { // player exists
            standings.insert(player, wins - 1);
        }
    }

    public void printStandings() {
        standings.printLeaderboard();
    }

}
