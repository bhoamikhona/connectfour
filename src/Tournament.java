public class Tournament {
    
    private TournamentQueue matchQueue;
    private AVLTree standings;
    private int id;

    // reference to player so we can rebuild standings
    private PlayerProfile[] players;

    public Tournament(int id, PlayerProfile[] players) {
        this.matchQueue = new TournamentQueue(players);
        this.standings = new AVLTree();
        this.players = players;

        // Initialize standings using current player wins
        // wins are stored in PlayerProfile, AVLTree just orders them
        for (PlayerProfile player : players) {
            standings.insert(player, player.getWins());
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

    /**
     * Win updates the PlayerProfile and then we rebuild the AVL standings i.e. ordering
     * matches updated wins.
     */
    public void addWin(PlayerProfile player) {
        // Update player stats (this increments wins in PlayerProfile)
        player.recordOverallResult(1);

        // Rebuild standings AVL so ordering matches updated wins
        AVLTree newTree = new AVLTree();

        for (PlayerProfile p : players) {
            newTree.insert(p, p.getWins());
        }

        standings = newTree;
    }

    /**
     * A loss does not subtract wins.
     * It simply updates the PlayerProfile losses, then rebuilds standings.
     */
    public void addLoss(PlayerProfile player) {
        // update player stasts (increment losses in PlayerProfile)
        player.recordOverallResult(-1);

        AVLTree newTree = new AVLTree();

        for (PlayerProfile p : players) {
            newTree.insert(p, p.getWins());
        }

        standings = newTree;
    }

    public void printStandings() {
        standings.printLeaderboard();
    }

}
