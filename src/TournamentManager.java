import java.util.HashMap;
import java.util.Map;


/**
 * In-memory registry for tournaments created during the current program run.
 *
 * After you run: {@code create tournament ...}, the Tournament object is stored here.
 *
 * Commands like {@code start tournament <id>} and {@code tournament standings <id>}
 * retrieve the same Tournament instance from this registry.
 *
 * Note: This registry is NOT persisted to disk. Only player profiles are persisted.
 */
public class TournamentManager {

    /** Map of tournament id -> tournament object (session-only). */
    private static Map<Integer, Tournament> tournaments = new HashMap<>();

    /**
     * Returns the tournament for the given id.
     *
     * @param id tournament id
     * @return Tournament if found, otherwise null
     */
    public static Tournament getTournament(int id) {
        return tournaments.get(id);
    }

    /**
     * Adds (or replaces) a tournament in the registry.
     *
     * @param tournament tournament instance to register
     */
    public static void addTournament(Tournament tournament) {
        tournaments.put(tournament.getId(), tournament);
    }

    /**
     * Removes a tournament from the registry (optional helper).
     *
     * @param id tournament id
     */
    public static void removeTournament(int id) {
        tournaments.remove(id);
    }

    /**
     * Checks whether a tournament id exists in this registry (optional helper).
     *
     * @param id tournament id
     * @return true if present, false otherwise
     */
    public static boolean tournamentExists(int id) {
        return tournaments.containsKey(id);
    }
}