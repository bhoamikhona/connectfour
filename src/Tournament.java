public class Tournament {
    
    private TournamentQueue matchQueue;
    private AVLTree standings;
    private int id;
    public Tournament(int id, PlayerProfile[] players) {
        this.matchQueue = new TournamentQueue(players);
        this.standings = new AVLTree();
        for (PlayerProfile player : players) {
            standings.insert(player, 0); // Initialize with 0 wins
        }
        this.id = id;
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
    public void addWin(PlayerProfile player) {
        int wins = standings.remove(player);
        if (wins != Integer.MIN_VALUE) { // player exists
            standings.insert(player, wins + 1);
        }
    }
    public void addLoss(PlayerProfile player) {
        int wins = standings.remove(player);
        if (wins != Integer.MIN_VALUE) { // player exists
            standings.insert(player, wins - 1);
        }
    }

    public void printStandings() {
        standings.printLeaderboard();
    }

}
