public class TournamentQueue {
    private Match[] matches;
    private int size;
    private int head;
    private PlayerProfile[] players;

    public TournamentQueue(PlayerProfile[] players) {
        this.matches = new Match[players.length * (players.length - 1) / 2];
        this.players = players;
        this.size = 0;
        this.head = 0;

        generateRoundRobinMatchs();
    }

    private void generateRoundRobinMatchs() {
        int n = players.length;

        // If odd number of players, add a dummy
        boolean hasDummy = (n % 2 != 0);
        PlayerProfile[] tempPlayers;

        if (hasDummy) {
            tempPlayers = new PlayerProfile[n + 1];
            System.arraycopy(players, 0, tempPlayers, 0, n);
            tempPlayers[n] = null; // Dummy player
            n++;
        } else {
            tempPlayers = players.clone();
        }

        int rounds = n - 1;
        int matchesPerRound = n / 2;

        for (int round = 0; round < rounds; round++) {
            for (int i = 0; i < matchesPerRound; i++) {
                PlayerProfile p1 = tempPlayers[i];
                PlayerProfile p2 = tempPlayers[n - 1 - i];

                if (p1 != null && p2 != null) {
                    enqueue(new Match(p1, p2));
                }
            }

            // Rotate players (except index 0)
            PlayerProfile last = tempPlayers[n - 1];
            for (int i = n - 1; i > 1; i--) {
                tempPlayers[i] = tempPlayers[i - 1];
            }
            tempPlayers[1] = last;
        }
    }

    private void enqueue(Match match) {
        if (size == matches.length) {
            throw new IllegalStateException("TournamentQueue is full");
        }
        int tail = (head + size) % matches.length;
        matches[tail] = match;
        size++;
    }

    public Match dequeue() {
        if (size == 0) {
            throw new IllegalStateException("TournamentQueue is empty");
        }
        Match match = matches[head];
        head = (head + 1) % matches.length;
        size--;
        return match;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == matches.length;
    }

    public Match peek() {
        if (size == 0) {
            return null;
        }
        return matches[head];
    }

    public void clear() {
        size = 0;
        head = 0;
    }
}
