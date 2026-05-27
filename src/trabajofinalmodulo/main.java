package trabajofinalmodulo;

import javax.swing.*;
import java.awt.*;

public class main extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// ── Colores exactos de tu diseño ─────────────────────────
    static final Color BG         = new Color(10, 10, 30);
    static final Color CELL_BG    = new Color(13, 13, 40);
    static final Color GRID_CLR   = new Color(220, 80, 220);
    static final Color X_CLR      = new Color(0, 200, 255);
    static final Color O_CLR      = new Color(220, 80, 220);

    public main() {
        setTitle("TRES EN RAYA - Modo 2 Jugadores");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        
        // Panel principal
        MenuPanel menuPanel = new MenuPanel();
        add(menuPanel);

        pack();
        setLocationRelativeTo(null); // Centrar en la pantalla
    }

    // ════════════════════════════════════════════════════════
    //  MENÚ PRINCIPAL MULTIJUGADOR
    // ════════════════════════════════════════════════════════
    class MenuPanel extends JPanel {
        
        MenuPanel() {
            setBackground(BG);
            setPreferredSize(new Dimension(500, 600));
            setLayout(null); // Diseño libre

            // Título
            JLabel title = new JLabel("TRES EN RAYA", SwingConstants.CENTER);
            title.setFont(new Font("Dialog", Font.BOLD, 34));
            title.setForeground(GRID_CLR);
            title.setBounds(0, 50, 500, 50);
            add(title);
            
            // Subtítulo
            JLabel subtitle = new JLabel("MODO EN RED - 3 JUGADORES", SwingConstants.CENTER);
            subtitle.setFont(new Font("Dialog", Font.BOLD, 14));
            subtitle.setForeground(X_CLR);
            subtitle.setBounds(0, 95, 500, 20);
            add(subtitle);

            // Logo tablero pequeño decorativo
            LogoPanel logo = new LogoPanel();
            logo.setBounds(175, 140, 150, 150);
            add(logo);

            // Botón CREAR PARTIDA (Para la PC 1)
            JButton hostBtn = menuButton("▶   CREAR PARTIDA (Anfitrión)", WIN_CLR());
            hostBtn.setBounds(125, 320, 250, 48);
            hostBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Aquí iría el código para abrir el Servidor (Sockets)"));
            add(hostBtn);

            // Botón UNIRSE (Para la PC 2 y 3)
            JButton joinBtn = menuButton("🔗   UNIRSE A PARTIDA (Invitado)", X_CLR);
            joinBtn.setBounds(125, 380, 250, 48);
            joinBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Aquí pedirías la IP del Anfitrión para conectarte"));
            add(joinBtn);

            // Botón SALIR
            JButton exitBtn = menuButton("✕   SALIR", new Color(180, 60, 60));
            exitBtn.setBounds(125, 440, 250, 48);
            exitBtn.addActionListener(e -> System.exit(0));
            add(exitBtn);

            // Créditos
            JLabel credit = new JLabel("ESPERANDO CONEXIÓN...", SwingConstants.CENTER);
            credit.setFont(new Font("Dialog", Font.PLAIN, 11));
            credit.setForeground(new Color(100, 100, 130));
            credit.setBounds(0, 530, 500, 20);
            add(credit);
        }

        // Color verde para el botón de anfitrión
        Color WIN_CLR() { return new Color(0, 255, 160); }

        // Método para dibujar los botones con el estilo de tu foto
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
    }

    // Mini tablero decorativo
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
    //  PUNTO DE ARRANQUE
    // ════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new main().setVisible(true);
        });
    }
}