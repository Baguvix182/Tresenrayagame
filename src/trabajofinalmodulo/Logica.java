package trabajofinalmodulo;

public class Logica {
    private String[] board = new String[9];
    private String current = "X";
    private int scoreX = 0, scoreO = 0;
    private boolean gameOver = false;
    private int[] winLine = null;

    private final int[][] wins = {
        {0,1,2},{3,4,5},{6,7,8},
        {0,3,6},{1,4,7},{2,5,8},
        {0,4,8},{2,4,6}
    };

    public Logica() { 
        resetGame(); 
    }

    public void resetGame() {
        for (int i = 0; i < 9; i++) board[i] = "";
        current = "X";
        gameOver = false;
        winLine = null;
    }

    public String[] getBoard() { return board; }
    public String getCurrent() { return current; }
    public void setCurrent(String current) { this.current = current; }
    public int getScoreX() { return scoreX; }
    public int getScoreO() { return scoreO; }
    public void incrementScoreX() { scoreX++; }
    public void incrementScoreO() { scoreO++; }
    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
    public int[] getWinLine() { return winLine; }
    public void setWinLine(int[] winLine) { this.winLine = winLine; }

    public int[] checkWinner() {
        for (int[] w : wins)
            if (!board[w[0]].isEmpty() && board[w[0]].equals(board[w[1]]) && board[w[0]].equals(board[w[2]]))
                return w;
        return null;
    }

    public boolean isFull() {
        for (String s : board) if (s.isEmpty()) return false;
        return true;
    }
}