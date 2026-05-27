package trabajofinalmodulo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.sound.sampled.*;

public class main extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// ── Colores estilo Neon/Arcade ─────────────────────────
    static final Color BG         = new Color(10, 10, 30);
    static final Color CELL_BG    = new Color(13, 13, 40);
    static final Color GRID_CLR   = new Color(220, 80, 220);
    static final Color X_CLR      = new Color(0, 200, 255);
    static final Color O_CLR      = new Color(220, 80, 220);
    static final Color WIN_CLR    = new Color(0, 255, 160);
    static final Color AVATAR_X   = new Color(0, 200, 255);
    static final Color AVATAR_O   = new Color(220, 80, 220);
    static final Color TEXT_WHITE = new Color(240, 240, 255);
    static final Color TEXT_GREY  = new Color(160, 160, 180);

    // ── Estado del Juego ───────────────────────────────────
    String[]  board    = new String[9];
    String    current  = "X";
    int       scoreX   = 0, scoreO = 0;
    boolean   gameOver = false;
    int[]     winLine  = null;

    int[][] wins = {
        {0,1,2},{3,4,5},{6,7,8},
        {0,3,6},{1,4,7},{2,5,8},
        {0,4,8},{2,4,6}
    };

    // ── Sistema de Audio Retro ─────────────────────────────
    boolean          musicOn      = false;
    Thread           musicThread;
    volatile boolean musicRunning = false;

    // ── Pantallas (CardLayout) ─────────────────────────────
    CardLayout   cards;
    JPanel       root;
    MenuPanel    menuPanel;
    GamePanel    gamePanel;

    public main() {
        setTitle("TRES EN RAYA - Retro Arcade Edition");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        cards = new CardLayout();
        root  = new JPanel(cards);
        root.setBackground(BG);

        menuPanel = new MenuPanel();
        gamePanel = new GamePanel();

        root.add(menuPanel, "MENU");
        root.add(gamePanel, "GAME");

        add(root);
        cards.show(root, "MENU");

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ════════════════════════════════════════════════════════
    //  MENÚ PRINCIPAL
    // ════════════════════════════════════════════════════════
    class MenuPanel extends JPanel {
        JButton musicBtn;

        MenuPanel() {
            setBackground(BG);
            setPreferredSize(new Dimension(500, 600));
            setLayout(null);
            buildContent();
        }

        void buildContent() {
            // Título
            JLabel title = new JLabel("TRES EN RAYA", SwingConstants.CENTER);
            title.setFont(new Font("Dialog", Font.BOLD, 34));
            title.setForeground(GRID_CLR);
            title.setBounds(0, 80, 500, 50);
            add(title);

            // Logo decorativo
            LogoPanel logo = new LogoPanel();
            logo.setBounds(175, 150, 150, 150);
            add(logo);

            // Botón JUGAR
            JButton playBtn = menuButton("▶   JUGAR", GRID_CLR);
            playBtn.setBounds(150, 355, 200, 48);
            playBtn.addActionListener(e -> {
                resetGame();
                cards.show(root, "GAME");
                gamePanel.requestFocusInWindow();
            });
            add(playBtn);

            // Botón MÚSICA
            musicBtn = menuButton("♪   MÚSICA: OFF", X_CLR);
            musicBtn.setBounds(150, 415, 200, 48);
            musicBtn.addActionListener(e -> toggleMusic());
            add(musicBtn);

            // Botón SALIR
            JButton exitBtn = menuButton("✕   SALIR", new Color(180, 60, 60));
            exitBtn.setBounds(150, 475, 200, 48);
            exitBtn.addActionListener(e -> System.exit(0));
            add(exitBtn);

            // Créditos
            JLabel credit = new JLabel("8-BIT CHIPTUNE LOOP", SwingConstants.CENTER);
            credit.setFont(new Font("Dialog", Font.PLAIN, 11));
            credit.setForeground(new Color(100, 100, 130));
            credit.setBounds(0, 555, 500, 20);
            add(credit);
        }

        JButton menuButton(String text, Color col) {
            JButton b = new JButton(text) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color bg = getModel().isPressed()
                        ? new Color(col.getRed(), col.getGreen(), col.getBlue(), 80)
                        : getModel().isRollover()
                        ? new Color(col.getRed(), col.getGreen(), col.getBlue(), 40)
                        : new Color(col.getRed(), col.getGreen(), col.getBlue(), 15);
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 180));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            b.setFont(new Font("Dialog", Font.BOLD, 13));
            b.setForeground(col);
            b.setBackground(BG);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setContentAreaFilled(false);
            b.setOpaque(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return b;
        }

        void updateMusicBtn() {
            if (musicBtn != null)
                musicBtn.setText(musicOn ? "♪   MÚSICA: ON" : "♪   MÚSICA: OFF");
        }
    }

    class LogoPanel extends JPanel {
        LogoPanel() { setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int W = getWidth(), H = getHeight();
            g2.setColor(CELL_BG);
            g2.fillRoundRect(0, 0, W, H, 6, 6);
            g2.setColor(GRID_CLR);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, W-2, H-2, 6, 6);
            g2.setColor(new Color(220, 80, 220, 100));
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(W/3, 4, W/3, H-4);
            g2.drawLine(2*W/3, 4, 2*W/3, H-4);
            g2.drawLine(4, H/3, W-4, H/3);
            g2.drawLine(4, 2*H/3, W-4, 2*H/3);
            int p = 8;
            g2.setColor(X_CLR);
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(p+4, p+4, W/3-p, H/3-p);
            g2.drawLine(W/3-p, p+4, p+4, H/3-p);
            g2.setColor(O_CLR);
            g2.setStroke(new BasicStroke(3f));
            g2.drawOval(W/3+p, H/3+p, W/3-p*2, H/3-p*2);
            g2.dispose();
        }
    }

    // ════════════════════════════════════════════════════════
    //  PANTALLA DE JUEGO
    // ════════════════════════════════════════════════════════
    class GamePanel extends JPanel {
        BoardPanel boardPanel;
        JLabel     statusLabel, turnLabel, scoreLbl;
        JButton    backBtn, resetBtn, musicBtn2;

        GamePanel() {
            setBackground(BG);
            setPreferredSize(new Dimension(500, 600));
            setLayout(null);
            build();
        }

        void build() {
            AvatarPanel av1 = new AvatarPanel(AVATAR_X, false);
            av1.setBounds(60, 30, 56, 56);
            add(av1);

            JLabel lbl1 = new JLabel("JUGADOR1", SwingConstants.CENTER);
            lbl1.setFont(new Font("Dialog", Font.PLAIN, 10));
            lbl1.setForeground(TEXT_GREY);
            lbl1.setBounds(30, 92, 116, 16);
            add(lbl1);

            scoreLbl = new JLabel("0 - 0", SwingConstants.CENTER);
            scoreLbl.setFont(new Font("Dialog", Font.BOLD, 40));
            scoreLbl.setForeground(TEXT_WHITE);
            scoreLbl.setBounds(150, 30, 200, 56);
            add(scoreLbl);

            AvatarPanel av2 = new AvatarPanel(AVATAR_O, true);
            av2.setBounds(384, 30, 56, 56);
            add(av2);

            JLabel lbl2 = new JLabel("JUGADOR2", SwingConstants.CENTER);
            lbl2.setFont(new Font("Dialog", Font.PLAIN, 10));
            lbl2.setForeground(TEXT_GREY);
            lbl2.setBounds(354, 92, 116, 16);
            add(lbl2);

            boardPanel = new BoardPanel();
            boardPanel.setBounds(40, 120, 420, 380);
            add(boardPanel);

            statusLabel = new JLabel(" ", SwingConstants.CENTER);
            statusLabel.setFont(new Font("Dialog", Font.BOLD, 14));
            statusLabel.setForeground(WIN_CLR);
            statusLabel.setBounds(0, 510, 500, 22);
            add(statusLabel);

            turnLabel = new JLabel("TURNO: JUGADOR 1", SwingConstants.CENTER);
            turnLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
            turnLabel.setForeground(X_CLR);
            turnLabel.setBounds(0, 532, 500, 18);
            add(turnLabel);

            backBtn = smallBtn("← MENÚ", TEXT_GREY);
            backBtn.setBounds(20, 562, 110, 30);
            backBtn.addActionListener(e -> cards.show(root, "MENU"));
            add(backBtn);

            resetBtn = smallBtn("↺ REINICIAR", GRID_CLR);
            resetBtn.setBounds(190, 562, 120, 30);
            resetBtn.addActionListener(e -> { resetGame(); gamePanel.repaintBoard(); });
            add(resetBtn);

            musicBtn2 = smallBtn("♪ MÚSICA", X_CLR);
            musicBtn2.setBounds(370, 562, 110, 30);
            musicBtn2.addActionListener(e -> toggleMusic());
            add(musicBtn2);
        }

        void repaintBoard() {
            boardPanel.repaint();
            scoreLbl.setText(scoreX + " - " + scoreO);
            statusLabel.setText(" ");
            statusLabel.setForeground(WIN_CLR);
            turnLabel.setText("TURNO: JUGADOR 1");
            turnLabel.setForeground(X_CLR);
        }

        void updateScore() { scoreLbl.setText(scoreX + " - " + scoreO); }

        void updateMusicBtn() {
            if (musicBtn2 != null)
                musicBtn2.setText(musicOn ? "♪ ON" : "♪ OFF");
        }

        JButton smallBtn(String text, Color col) {
            JButton b = new JButton(text) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color bg = getModel().isRollover()
                        ? new Color(col.getRed(), col.getGreen(), col.getBlue(), 40)
                        : new Color(col.getRed(), col.getGreen(), col.getBlue(), 10);
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 160));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            b.setFont(new Font("Dialog", Font.BOLD, 11));
            b.setForeground(col);
            b.setBackground(BG);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setContentAreaFilled(false);
            b.setOpaque(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return b;
        }

		public void setTurn(String string, Object object) {
			// TODO Auto-generated method stub
			
		}

		public void setStatus(String string, Color winClr) {
			// TODO Auto-generated method stub
			
		}
    }

    class AvatarPanel extends JPanel {
        Color col; boolean filled;
        AvatarPanel(Color c, boolean f) { col = c; filled = f; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int W = getWidth(), H = getHeight();
            g2.setColor(col);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(1, 1, W-2, H-2);
            int hw = W/4, hh = H/4;
            int hx = W/2 - hw/2, hy = H/6;
            g2.drawOval(hx, hy, hw, hh);
            int bw = W*3/5, bh = H*2/5;
            int bx = W/2 - bw/2, by = H/2;
            g2.drawArc(bx, by, bw, bh, 0, 180);
            g2.dispose();
        }
    }

    // ════════════════════════════════════════════════════════
    //  TABLERO DE JUEGO
    // ════════════════════════════════════════════════════════
    class BoardPanel extends JPanel {
        static final int PAD = 14;

        BoardPanel() {
            setBackground(CELL_BG);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    int cw = getWidth() / 3, ch = getHeight() / 3;
                    int col = e.getX() / cw, row = e.getY() / ch;
                    makeMove(row * 3 + col);
                }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int W = getWidth(), H = getHeight();
            int cw = W / 3, ch = H / 3;

            for (int r = 0; r < 3; r++)
                for (int c = 0; c < 3; c++) {
                    g2.setColor(CELL_BG);
                    g2.fillRect(c * cw + 1, r * ch + 1, cw - 2, ch - 2);
                }

            g2.setColor(GRID_CLR);
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawRect(1, 1, W - 2, H - 2);

            g2.setColor(new Color(220, 80, 220, 160));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(cw, 2, cw, H - 2);
            g2.drawLine(cw * 2, 2, cw * 2, H - 2);
            g2.drawLine(2, ch, W - 2, ch);
            g2.drawLine(2, ch * 2, W - 2, ch * 2);

            for (int i = 0; i < 9; i++) {
                int row = i / 3, col = i % 3;
                int x = col * cw + PAD;
                int y = row * ch + PAD;
                int bw = cw - PAD * 2;
                int bh = ch - PAD * 2;

                boolean isWin = false;
                if (winLine != null) for (int wi : winLine) if (wi == i) { isWin = true; break; }

                if ("X".equals(board[i])) {
                    Color c = isWin ? WIN_CLR : X_CLR;
                    g2.setColor(c);
                    g2.setStroke(new BasicStroke(isWin ? 6f : 4.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(x, y, x + bw, y + bh);
                    g2.drawLine(x + bw, y, x, y + bh);
                    g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 60));
                    g2.setStroke(new BasicStroke(isWin ? 10f : 8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(x, y, x + bw, y + bh);
                    g2.drawLine(x + bw, y, x, y + bh);

                } else if ("O".equals(board[i])) {
                    Color c = isWin ? new Color(255, 160, 255) : O_CLR;
                    g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 50));
                    g2.setStroke(new BasicStroke(isWin ? 12f : 10f));
                    g2.drawOval(x, y, bw, bh);
                    g2.setColor(c);
                    g2.setStroke(new BasicStroke(isWin ? 5.5f : 4f));
                    g2.drawOval(x, y, bw, bh);
                }
            }
            g2.dispose();
        }
    }

    // ════════════════════════════════════════════════════════
    //  LÓGICA DEL JUEGO
    // ════════════════════════════════════════════════════════
    void makeMove(int i) {
        if (gameOver || i < 0 || i > 8 || !board[i].isEmpty()) return;
        board[i] = current;
        
        // Sonidos breves de 8 bits al colocar fichas
        playTone(current.equals("X") ? 587 : 698, 65);

        int[] line = checkWinner();
        if (line != null) {
            winLine = line;
            gameOver = true;
            String w = current.equals("X") ? "1" : "2";
            gamePanel.setStatus("🎉  ¡JUGADOR " + w + " GANA!", WIN_CLR);
            gamePanel.setTurn(" ", TEXT_GREY);
            if (current.equals("X")) scoreX++; else scoreO++;
            gamePanel.updateScore();
            playWinSound();
        } else if (isFull()) {
            gameOver = true;
            gamePanel.setStatus("EMPATE", new Color(255, 180, 0));
            gamePanel.setTurn(" ", TEXT_GREY);
        } else {
            current = current.equals("X") ? "O" : "X";
            String p = current.equals("X") ? "1" : "2";
            gamePanel.setStatus(" ", WIN_CLR);
            gamePanel.setTurn("TURNO: JUGADOR " + p, current.equals("X") ? X_CLR : O_CLR);
        }
        gamePanel.boardPanel.repaint();
    }

    int[] checkWinner() {
        for (int[] w : wins)
            if (!board[w[0]].isEmpty() && board[w[0]].equals(board[w[1]]) && board[w[0]].equals(board[w[2]]))
                return w;
        return null;
    }

    boolean isFull() { for (String s : board) if (s.isEmpty()) return false; return true; }

    void resetGame() {
        board   = new String[9];
        for (int i = 0; i < 9; i++) board[i] = "";
        current  = "X";
        gameOver = false;
        winLine  = null;
    }

    // ════════════════════════════════════════════════════════
    //  AUDIO: REPRODUCTOR RETRO ONDA CUADRADA (CHIPTUNE)
    // ════════════════════════════════════════════════════════
    void playTone(int freq, int ms) {
        new Thread(() -> {
            try {
                float sr = 44100f;
                int n = (int)(sr * ms / 1000);
                byte[] d = new byte[n];
                
                if (freq <= 20) {
                    for (int i = 0; i < n; i++) d[i] = 0; // Silencio para pausas de notas
                } else {
                    for (int i = 0; i < n; i++) {
                        double env = 1.0 - (double) i / n;
                        // Transformador Math.signum para generar una onda cuadrada retro limpia
                        double wave = Math.signum(Math.sin(2 * Math.PI * i * freq / sr));
                        d[i] = (byte)(wave * 25 * env); // Volumen balanceado
                    }
                }
                AudioFormat fmt = new AudioFormat(sr, 8, 1, true, true);
                SourceDataLine l = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, fmt));
                l.open(fmt); l.start(); l.write(d, 0, d.length); l.drain(); l.close();
            } catch (Exception ignored) {}
        }).start();
    }

    void playWinSound() {
        new Thread(() -> {
            int[] notes = {523, 659, 784, 1047};
            for (int note : notes) {
                playTone(note, 90);
                try { Thread.sleep(100); } catch (InterruptedException e) { break; }
            }
        }).start();
    }

    void toggleMusic() {
        musicOn = !musicOn;
        menuPanel.updateMusicBtn();
        gamePanel.updateMusicBtn();
        if (musicOn) {
            musicRunning = true;
            musicThread = new Thread(() -> {
                // Melodía cíclica original alegre inspirada en los juegos arcade/retro
                // Formato: {Frecuencia_Hz, Duración_ms}
                int[][] retroLoop = {
                    {523, 160}, {659, 160}, {784, 160}, {659, 160},
                    {587, 160}, {698, 160}, {880, 160}, {698, 160},
                    {659, 160}, {784, 160}, {1047, 160}, {784, 160},
                    {587, 320}, {494, 320},
                    
                    {523, 160}, {659, 160}, {784, 160}, {659, 160},
                    {880, 320}, {784, 320}, {659, 320}, {523, 160},
                    {587, 160}, {494, 160}, {440, 320}, {1, 160} // Nota 1 = Silencio breve
                };
                int step = 0;
                while (musicRunning) {
                    int[] note = retroLoop[step % retroLoop.length];
                    playTone(note[0], note[1] - 15); // Staccato para separar notas bien
                    try { Thread.sleep(note[1]); } catch (InterruptedException e) { break; }
                    step++;
                }
            });
            musicThread.setDaemon(true);
            musicThread.start();
        } else {
            musicRunning = false;
            if (musicThread != null) musicThread.interrupt();
        }
    }

    // ════════════════════════════════════════════════════════
    //  MAIN
    // ════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(main::new);
    }
}