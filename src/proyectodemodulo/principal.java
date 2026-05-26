package proyectodemodulo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.sound.sampled.*;

public class principal extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// ── Colores neón ──────────────────────────────────────────
    static final Color BG        = new Color(10, 10, 26);
    static final Color GRID_CLR  = new Color(255, 64, 255);
    static final Color X_CLR     = new Color(0, 212, 255);
    static final Color O_CLR     = new Color(255, 64, 255);
    static final Color WIN_CLR   = new Color(0, 255, 153);
    static final Color TEXT_CLR  = new Color(200, 200, 200);

    // ── Estado del juego ─────────────────────────────────────
    String[] board     = new String[9];
    String   current   = "X";
    int[]    scoreX    = {0};
    int[]    scoreO    = {0};
    boolean  gameOver  = false;
    int[]    winLine   = null;

    // Combinaciones ganadoras
    int[][] wins = {
        {0,1,2},{3,4,5},{6,7,8},
        {0,3,6},{1,4,7},{2,5,8},
        {0,4,8},{2,4,6}
    };

    // ── Componentes UI ───────────────────────────────────────
    BoardPanel boardPanel;
    JLabel     statusLabel;
    JLabel     turnLabel;
    JLabel     scoreLabel;
    JButton    resetBtn;
    JButton    musicBtn;

    // ── Música ───────────────────────────────────────────────
    boolean    musicOn   = false;
    Thread     musicThread;
    volatile   boolean   musicRunning = false;

    // ─────────────────────────────────────────────────────────
    public principal() {
        setTitle("TRES EN RAYA — Neón");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(10, 10));

        initBoard();
        buildUI();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    void initBoard() {
        for (int i = 0; i < 9; i++) board[i] = "";
    }

    // ── Construcción de la interfaz ──────────────────────────
    void buildUI() {
        // ---- Panel superior: marcador
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        top.setBackground(BG);

        JLabel p1 = neonLabel("JUGADOR 1", X_CLR, 11);
        scoreLabel = neonLabel("0  -  0", Color.WHITE, 36);
        JLabel p2 = neonLabel("JUGADOR 2", O_CLR, 11);

        top.add(p1);
        top.add(scoreLabel);
        top.add(p2);
        add(top, BorderLayout.NORTH);

        // ---- Tablero central
        boardPanel = new BoardPanel();
        boardPanel.setPreferredSize(new Dimension(330, 330));
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        center.setBackground(BG);
        center.add(boardPanel);
        add(center, BorderLayout.CENTER);

        // ---- Panel inferior: estado + botones
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBackground(BG);

        statusLabel = neonLabel(" ", WIN_CLR, 13);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        turnLabel = neonLabel("TURNO: JUGADOR 1", X_CLR, 11);
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setBackground(BG);

        resetBtn = neonButton("↺  REINICIAR", GRID_CLR);
        resetBtn.addActionListener(e -> resetGame());

        musicBtn = neonButton("♪  MÚSICA ON", X_CLR);
        musicBtn.addActionListener(e -> toggleMusic());

        btnRow.add(resetBtn);
        btnRow.add(musicBtn);

        bottom.add(statusLabel);
        bottom.add(Box.createVerticalStrut(4));
        bottom.add(turnLabel);
        bottom.add(Box.createVerticalStrut(10));
        bottom.add(btnRow);
        bottom.add(Box.createVerticalStrut(14));

        add(bottom, BorderLayout.SOUTH);
    }

    // ── Lógica del juego ─────────────────────────────────────
    void makeMove(int i) {
        if (gameOver || !board[i].isEmpty()) return;
        board[i] = current;
        playTone(current.equals("X") ? 440 : 660, 80);

        int[] line = checkWinner();
        if (line != null) {
            winLine = line;
            gameOver = true;
            String winner = current.equals("X") ? "1" : "2";
            statusLabel.setText("🎉  ¡JUGADOR " + winner + " GANA!");
            turnLabel.setText(" ");
            if (current.equals("X")) scoreX[0]++; else scoreO[0]++;
            scoreLabel.setText(scoreX[0] + "  -  " + scoreO[0]);
            playWinSound();
        } else if (isFull()) {
            gameOver = true;
            statusLabel.setText("EMPATE");
            statusLabel.setForeground(new Color(255, 170, 0));
            turnLabel.setText(" ");
        } else {
            current = current.equals("X") ? "O" : "X";
            String p = current.equals("X") ? "1" : "2";
            turnLabel.setText("TURNO: JUGADOR " + p);
            turnLabel.setForeground(current.equals("X") ? X_CLR : O_CLR);
            statusLabel.setText(" ");
            statusLabel.setForeground(WIN_CLR);
        }
        boardPanel.repaint();
    }

    int[] checkWinner() {
        for (int[] w : wins) {
            if (!board[w[0]].isEmpty()
                && board[w[0]].equals(board[w[1]])
                && board[w[0]].equals(board[w[2]])) return w;
        }
        return null;
    }

    boolean isFull() {
        for (String s : board) if (s.isEmpty()) return false;
        return true;
    }

    void resetGame() {
        initBoard();
        current  = "X";
        gameOver = false;
        winLine  = null;
        statusLabel.setText(" ");
        statusLabel.setForeground(WIN_CLR);
        turnLabel.setText("TURNO: JUGADOR 1");
        turnLabel.setForeground(X_CLR);
        boardPanel.repaint();
    }

    // ── Sonido ───────────────────────────────────────────────
    void playTone(int freq, int durationMs) {
        new Thread(() -> {
            try {
                float sampleRate = 44100f;
                int samples = (int)(sampleRate * durationMs / 1000);
                byte[] data = new byte[samples];
                for (int i = 0; i < samples; i++) {
                    double angle = 2.0 * Math.PI * i * freq / sampleRate;
                    double envelope = 1.0 - (double)i / samples;
                    data[i] = (byte)(Math.sin(angle) * 60 * envelope);
                }
                AudioFormat fmt = new AudioFormat(sampleRate, 8, 1, true, true);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, fmt);
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(fmt); line.start();
                line.write(data, 0, data.length);
                line.drain(); line.close();
            } catch (Exception ignored) {}
        }).start();
    }

    void playWinSound() {
        int[] notes = {523, 659, 784, 1047};
        new Thread(() -> {
            for (int note : notes) {
                playTone(note, 130);
                try { Thread.sleep(140); } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    void toggleMusic() {
        musicOn = !musicOn;
        if (musicOn) {
            musicBtn.setText("♪  MÚSICA OFF");
            musicRunning = true;
            musicThread = new Thread(() -> {
                int[] melody = {220, 247, 262, 294, 330, 294, 262, 247};
                int step = 0;
                while (musicRunning) {
                    playTone(melody[step % melody.length], 250);
                    step++;
                    try { Thread.sleep(320); } catch (InterruptedException e) { break; }
                }
            });
            musicThread.setDaemon(true);
            musicThread.start();
        } else {
            musicBtn.setText("♪  MÚSICA ON");
            musicRunning = false;
            if (musicThread != null) musicThread.interrupt();
        }
    }

    // ── Helpers UI ───────────────────────────────────────────
    JLabel neonLabel(String text, Color color, int size) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Dialog", Font.BOLD, size));
        l.setForeground(color);
        return l;
    }

    JButton neonButton(String text, Color color) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2.setColor(color.darker().darker());
                else if (getModel().isRollover()) g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
                else g2.setColor(BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 140));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Dialog", Font.BOLD, 11));
        b.setForeground(color);
        b.setBackground(BG);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setPreferredSize(new Dimension(150, 36));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Panel del tablero ────────────────────────────────────
    class BoardPanel extends JPanel {
        static final int GAP = 10;

        BoardPanel() {
            setBackground(BG);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    int w = getWidth(), h = getHeight();
                    int cw = w / 3, ch = h / 3;
                    int col = e.getX() / cw;
                    int row = e.getY() / ch;
                    int idx = row * 3 + col;
                    if (idx >= 0 && idx < 9) makeMove(idx);
                }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int W = getWidth(), H = getHeight();
            int cw = W / 3, ch = H / 3;

            // Borde exterior
            g2.setColor(GRID_CLR);
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawRoundRect(2, 2, W-4, H-4, 6, 6);

            // Líneas internas
            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(new Color(255, 64, 255, 120));
            g2.drawLine(cw, 4, cw, H-4);
            g2.drawLine(cw*2, 4, cw*2, H-4);
            g2.drawLine(4, ch, W-4, ch);
            g2.drawLine(4, ch*2, W-4, ch*2);

            // Piezas
            for (int i = 0; i < 9; i++) {
                int row = i / 3, col = i % 3;
                int x = col * cw + GAP;
                int y = row * ch + GAP;
                int bw = cw - GAP*2;
                int bh = ch - GAP*2;

                boolean isWin = false;
                if (winLine != null) for (int wi : winLine) if (wi == i) { isWin = true; break; }

                if (board[i].equals("X")) {
                    Color c = isWin ? WIN_CLR : X_CLR;
                    g2.setColor(c);
                    g2.setStroke(new BasicStroke(isWin ? 5f : 3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(x, y, x+bw, y+bh);
                    g2.drawLine(x+bw, y, x, y+bh);
                } else if (board[i].equals("O")) {
                    Color c = isWin ? new Color(255, 180, 255) : O_CLR;
                    g2.setColor(c);
                    g2.setStroke(new BasicStroke(isWin ? 5f : 3.5f));
                    g2.drawOval(x, y, bw, bh);
                }
            }
        }
    }

    // ── Main ─────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(principal::new);
    }
}