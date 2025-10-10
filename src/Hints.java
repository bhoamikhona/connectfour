
/**
 * Hints class provides analysis for safe and unsafe moves.
 * It uses our custom LinkedList to store column indices that are safe or unsafe,
 * and recommends one column to play (favoring the center).
 */
public class Hints {
    public LinkedList safeCols;
    public LinkedList unsafeCols;
    public int recommendedCol;

    public Hints() {
        safeCols = new LinkedList();
        unsafeCols = new LinkedList();
        recommendedCol = -1;
    }

    public void print() {
        System.out.println("Safe columns: " + safeCols);
        System.out.println("Unsafe columns: " + unsafeCols);
        System.out.println("Recommended column: " + recommendedCol);
    }

    /**
     * analyzes the board for giving hints
     **/

    public static Hints getHints(char currentToken, char opponentToken, Board board) {
        Hints hints = new Hints();
        int cols = board.getCols();
        int rows = board.getRows();
        char[][] grid = board.getGrid();

        // === 1. Check for immediate winning move ===
        for (int c = 0; c < cols; c++) {
            if (board.isColumnFull(c)) continue;
            int r = -1;
            for (int i = rows - 1; i >= 0; i--) {
                if (grid[i][c] == ' ') {
                    r = i;
                    break;
                }
            }
            if (r == -1) continue;
            grid[r][c] = currentToken;
            if (board.isWinningMove(r, c)) {
                grid[r][c] = ' ';
                hints.recommendedCol = c;
                return hints; // immediate win
            }
            grid[r][c] = ' ';
        }

        // === 2. Block opponent's winning move ===
        for (int c = 0; c < cols; c++) {
            if (board.isColumnFull(c)) continue;
            int r = -1;
            for (int i = rows - 1; i >= 0; i--) {
                if (grid[i][c] == ' ') {
                    r = i;
                    break;
                }
            }
            if (r == -1) continue;
            grid[r][c] = opponentToken;
            if (board.isWinningMove(r, c)) {
                grid[r][c] = ' ';
                hints.recommendedCol = c;
                return hints; // block opponent
            }
            grid[r][c] = ' ';
        }

        // === 3. Look for "two in a row" pattern (horizontal/vertical/diagonal) ===
        for (int c = 0; c < cols; c++) {
            if (board.isColumnFull(c)) continue;
            int r = -1;
            for (int i = rows - 1; i >= 0; i--) {
                if (grid[i][c] == ' ') {
                    r = i;
                    break;
                }
            }
            if (r == -1) continue;
            grid[r][c] = currentToken;

            boolean twoInARow = false;
            // check 4 directions for a sequence of 2 tokens touching
            int[][] dirs = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
            for (int[] d : dirs) {
                int count = 1;
                count += countTokens(grid, rows, cols, r, c, d[0], d[1], currentToken);
                count += countTokens(grid, rows, cols, r, c, -d[0], -d[1], currentToken);
                if (count == 3) { // would create 3 in a row
                    twoInARow = true;
                    break;
                }
            }
            grid[r][c] = ' ';
            if (twoInARow) {
                hints.recommendedCol = c;
                return hints;
            }
        }

        // === 4. Otherwise, pick safest near-center column ===
        for (int c = 0; c < cols; c++) {
            if (board.isColumnFull(c)) hints.unsafeCols.add(c);
            else hints.safeCols.add(c);
        }

        if (!hints.safeCols.isEmpty()) {
            int center = cols / 2;
            int best = hints.safeCols.getFirst();
            int bestDist = Math.abs(best - center);
            for (int i = 0; i < hints.safeCols.size(); i++) {
                int col = hints.safeCols.get(i);
                int dist = Math.abs(col - center);
                if (dist < bestDist) {
                    best = col;
                    bestDist = dist;
                }
            }
            hints.recommendedCol = best;
        } else {
            hints.recommendedCol = cols / 2; // fallback center
        }

        return hints;
    }

    // helper to count consecutive tokens in one direction
    private static int countTokens(char[][] grid, int rows, int cols, int r, int c, int dr, int dc, char token) {
        int count = 0;
        int i = r + dr, j = c + dc;
        while (i >= 0 && i < rows && j >= 0 && j < cols && grid[i][j] == token) {
            count++;
            i += dr;
            j += dc;
        }
        return count;
    }
}

