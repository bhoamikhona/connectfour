import java.io.*;
import java.util.*;

/**
 * Stores all player profiles in a Map and persists it to a file.
 * Tracks "current logged-in user" in memory ONLY (not persisted).
 */
public class PlayerStore {
    private final String saveFilePath;
    private Map<String, PlayerProfile> players;
    private String currentUser; // NOT persisted

    public PlayerStore(String saveFilePath) {
        this.saveFilePath = saveFilePath;
        this.players = loadFromDisk(saveFilePath);
        this.currentUser = null;
    }

    // -------------------------
    // Commands
    // -------------------------

    public String register(String name) {
        name = normalizeName(name);
        if (!isValidName(name)) return "Invalid name. Use 1-20 letters/digits/_ only.";

        if (players.containsKey(name)) {
            return "Player already exists: " + name;
        }
        players.put(name, new PlayerProfile(name));
        save();
        return "Registered player: " + name;
    }

    public String login(String name) {
        name = normalizeName(name);
        if (!players.containsKey(name)) {
            return "No such player. Try: register " + name;
        }
        currentUser = name;
        return "Logged in as: " + name;
    }

    public String logout() {
        if (currentUser == null) return "No user is currently logged in.";
        String old = currentUser;
        currentUser = null;
        return "Logged out: " + old;
    }

    public String whoami() {
        return (currentUser == null) ? "(none)" : currentUser;
    }

    public String profile(String name) {
        name = normalizeName(name);
        PlayerProfile p = players.get(name);
        if (p == null) return "No such player: " + name;
        return p.prettyProfile();
    }

    public String leaderboardTop(int n) {
        if (n <= 0) return "N must be > 0.";

        List<PlayerProfile> list = new ArrayList<>(players.values());
        list.sort(new Comparator<PlayerProfile>() {
            public int compare(PlayerProfile a, PlayerProfile b) {
                if (a.getWins() != b.getWins()) return b.getWins() - a.getWins();
                return a.getName().compareToIgnoreCase(b.getName());
            }
        });

        StringBuilder sb = new StringBuilder();
        sb.append("Leaderboard (top ").append(n).append(" by wins)\n");
        sb.append("---------------------------------\n");
        int limit = Math.min(n, list.size());
        for (int i = 0; i < limit; i++) {
            PlayerProfile p = list.get(i);
            sb.append(String.format("%2d) %-12s  W:%d D:%d L:%d\n",
                    (i + 1), p.getName(), p.getWins(), p.getDraws(), p.getLosses()));
        }
        if (players.isEmpty()) sb.append("(no players)\n");
        return sb.toString();
    }

    // -------------------------
    // Used by Game / other modules
    // -------------------------

    public PlayerProfile getProfileObject(String name) {
        name = normalizeName(name);
        return players.get(name);
    }

    /**
     * Human vs Human:
     * resultForX: 1 win, 0 draw, -1 loss (from X player's perspective)
     */
    public void recordHumanVsHuman(String xName, String oName, int resultForX) {
        PlayerProfile x = getProfileObject(xName);
        PlayerProfile o = getProfileObject(oName);
        if (x == null || o == null) return;

        x.recordOverallResult(resultForX);
        o.recordOverallResult(-resultForX);
        save();
    }

    /**
     * Human vs AI: result from HUMAN perspective
     */
    public void recordHumanVsAI(String humanName, int resultForHuman) {
        PlayerProfile h = getProfileObject(humanName);
        if (h == null) return;

        h.recordAiResult(resultForHuman);
        save();
    }

    public void save() {
        saveToDisk(saveFilePath, players);
    }

    // -------------------------
    // Persistence
    // -------------------------

    @SuppressWarnings("unchecked")
    private Map<String, PlayerProfile> loadFromDisk(String path) {
        File f = new File(path);
        if (!f.exists()) return new HashMap<>();

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = in.readObject();
            if (obj instanceof Map) {
                return (Map<String, PlayerProfile>) obj;
            }
        } catch (Exception e) {
            System.out.println("Warning: could not load profiles file. Starting new. (" + e.getMessage() + ")");
        }
        return new HashMap<>();
    }

    private void saveToDisk(String path, Map<String, PlayerProfile> map) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(map);
        } catch (IOException e) {
            System.out.println("Warning: could not save profiles. (" + e.getMessage() + ")");
        }
    }

    // -------------------------
    // Helpers
    // -------------------------

    private String normalizeName(String name) {
        if (name == null) return "";
        return name.trim();
    }

    private boolean isValidName(String name) {
        if (name.length() < 1 || name.length() > 20) return false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            boolean ok = Character.isLetterOrDigit(c) || c == '_';
            if (!ok) return false;
        }
        return true;
    }
}
