import java.util.HashMap;
import java.util.Map;

public class TournamentManager {
    private static Map<Integer, Tournament> tournaments = new HashMap<>();

    public static Tournament getTournament(int id) {
        return tournaments.get(id);
    }

    public static void addTournament(Tournament tournament) {
        tournaments.put(tournament.getId(), tournament);
    }

    public static void removeTournament(int id) {
        tournaments.remove(id);
    }

    public static boolean tournamentExists(int id) {
        return tournaments.containsKey(id);
    }
}