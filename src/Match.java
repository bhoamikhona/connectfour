public class Match {
    private PlayerProfile player1;
    private PlayerProfile player2;
    public Match(PlayerProfile p1, PlayerProfile p2) {
        this.player1 = p1;
        this.player2 = p2;
    }

    public PlayerProfile getPlayer1() {
        return player1;
    }
    public PlayerProfile getPlayer2() {
        return player2;
    }

}
