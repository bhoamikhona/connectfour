
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
    /** analyzes the board for giving hints **/
    public static Hints getHints(char currentToken, char opponentToken, Board board) {
        Hints hints = new Hints();
        int cols = board.getCols();
        int rows = board.getRows();
        char [][] grid = board.getGrid();


        int col = 0;
        while (col < cols) {
            // Skip full columns
            boolean full = grid[0][col] != ' ';

            if (!full) {
                // find landing row
                int r = rows - 1;
                int row = -1;
                while (r >= 0 && row == -1) {
                    if (grid[r][col] == ' ') {
                        row = r;
                    }
                    r = r - 1;
                }

                if (row != -1) {
                    // simulate dropping our piece
                    grid[row][col] = currentToken;

                    // check if this move is winning for current player
                    boolean winning = board.isWinningMove(row, col);

                    // simulate opponent move on top of ours
                    grid[row][col] = opponentToken;
                    boolean opponentWin = board.isWinningMove(row, col);

                    // undo
                    grid[row][col] = ' ';

                    // classify safe/unsafe
                    if (opponentWin) {
                        hints.unsafeCols.add(col);
                    } else {
                        hints.safeCols.add(col);
                    }

                    // record immediate win candidate
                    if (winning && hints.recommendedCol == -1) {
                        hints.recommendedCol = col;
                    }
                }
            }
            col = col + 1;
        }

        // === If no winning move, check if we can block opponent ===
        if (hints.recommendedCol == -1) {
            int c = 0;
            while (c < cols && hints.recommendedCol == -1) {
                if (!board.isColumnFull(c)) {
                    int r = rows - 1;
                    int row = -1;
                    while (r >= 0 && row == -1) {
                        if (grid[r][c] == ' ') {
                            row = r;
                        }
                        r = r - 1;
                    }

                    if (row != -1) {
                        grid[row][c] = opponentToken;
                        if (board.isWinningMove(row, c)) {
                            hints.recommendedCol = c; // block
                        }
                        grid[row][c] = ' ';
                    }
                }
                c = c + 1;
            }
        }

        // === Otherwise, fallback to closest-to-center safe column ===
        if (hints.recommendedCol == -1 && !hints.safeCols.isEmpty()) {
            int center = cols / 2;
            int bestCol = hints.safeCols.getFirst();
            int bestDist = Math.abs(bestCol - center);

            int i = 0;
            while (i < hints.safeCols.size()) {
                int c = hints.safeCols.get(i);
                int dist = Math.abs(c - center);
                if (dist < bestDist || (dist == bestDist && c > bestCol)) {
                    bestCol = c;
                    bestDist = dist;
                }
                i = i + 1;
            }
            hints.recommendedCol = bestCol;
        }

        return hints;
    }
}
