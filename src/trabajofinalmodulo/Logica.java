package trabajofinalmodulo;

import java.util.Scanner;
import java.util.ArrayList;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;

public class Logica {
    public String[] board;       
    public boolean isXTurn;      
    public int scoreX;           
    public int scoreO;           
    public boolean gameOver;     
    public boolean contraRobot;  
    public int dificultadRobot;  

    public Logica() {
        this.board = new String[9];
        this.scoreX = 0;
        this.scoreO = 0;
        this.resetGame();
    }

    public void resetGame() {
        for (int i = 0; i < 9; ++i) {
            this.board[i] = String.valueOf(i + 1);
        }
        this.isXTurn = true;
        this.gameOver = false;
    }

    public void limpiarPantalla() {
        System.out.print("\033[H\033[2J\033[3J");
        System.out.flush();
    }

    // ========== JUEGO NORMAL ==========
    public void iniciarJuego(Scanner scanner, boolean contraRobot, int dificultad) {
        this.contraRobot = contraRobot;
        this.dificultadRobot = dificultad;
        this.resetGame();
        bucleJuegoLocal(scanner);
    }

    private void bucleJuegoLocal(Scanner scanner) {
        while (!gameOver) {
            limpiarPantalla(); 
            imprimirTablero();
            int index = -1;

            if (isXTurn) {
                System.out.println("\n  Turno de: " + Colores.X_CLR + "JUGADOR 1 (X)" + Colores.RESET);
                System.out.print("  Elige una casilla (1-9). ¡Tienes 10 segundos!: ");
                index = leerEntradaConLimite(scanner);
            } else {
                if (contraRobot) {
                    System.out.println("\n  Turno del ROBOT (O) ... Pensando...");
                    try { Thread.sleep(1000); } catch (InterruptedException e) { } 
                    index = obtenerMovimientoRobot();
                } else {
                    System.out.println("\n  Turno de: " + Colores.O_CLR + "JUGADOR 2 (O)" + Colores.RESET);
                    System.out.print("  Elige una casilla (1-9). ¡Tienes 10 segundos!: ");
                    index = leerEntradaConLimite(scanner);
                }
            }
            procesarMovimiento(index);
        }
        System.out.print("\nPresiona ENTER para retornar al Menú Principal...");
        scanner.nextLine(); scanner.nextLine(); 
    }

    // ========== JUEGO EN RED ==========
    public void iniciarJuegoRed(Scanner scanner, boolean esServidor, String ip) {
        this.resetGame();
        this.contraRobot = false;
        
        try {
            Socket socket;
            DataOutputStream out;
            DataInputStream in;

            if (esServidor) {
                limpiarPantalla();
                System.out.println(Colores.YELLOW + "  Iniciando Servidor..." + Colores.RESET);
                System.out.println("  Tu IP Local es: " + InetAddress.getLocalHost().getHostAddress());
                System.out.println("  Esperando a que el JUGADOR 2 (O) se conecte...");
                
                ServerSocket server = new ServerSocket(5000);
                socket = server.accept();
                System.out.println(Colores.WIN_CLR + "  ¡JUGADOR 2 CONECTADO!" + Colores.RESET);
                server.close();
            } else {
                limpiarPantalla();
                System.out.println(Colores.YELLOW + "  Conectando al Servidor en IP: " + ip + "..." + Colores.RESET);
                socket = new Socket(ip, 5000);
                System.out.println(Colores.WIN_CLR + "  ¡CONECTADO AL JUGADOR 1!" + Colores.RESET);
            }

            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            try { Thread.sleep(1500); } catch (InterruptedException e) {}

            boolean miTurno = esServidor; // Servidor (X) inicia, Cliente (O) espera
            String miFicha = esServidor ? "X" : "O";

            while (!gameOver) {
                limpiarPantalla();
                imprimirTablero();

                if (miTurno) {
                    System.out.println("\n  " + Colores.WIN_CLR + "¡ES TU TURNO! Juegas con la ficha (" + miFicha + ")" + Colores.RESET);
                    System.out.print("  Elige una casilla disponible (1-9): ");
                    
                    int index = leerEntradaNormal(scanner);
                    while (index < 0 || index >= 9 || board[index].equals("X") || board[index].equals("O")) {
                        System.out.print(Colores.X_CLR + "  Casilla inválida u ocupada. Intenta de nuevo: " + Colores.RESET);
                        index = leerEntradaNormal(scanner);
                    }
                    
                    procesarMovimiento(index);
                    out.writeInt(index); // Enviar movimiento por red
                    out.flush();
                } else {
                    System.out.println("\n  " + Colores.GREY + "Esperando el turno del oponente (" + (miFicha.equals("X") ? "O" : "X") + ")..." + Colores.RESET);
                    int indexOponente = in.readInt(); // Recibir movimiento por red
                    procesarMovimiento(indexOponente);
                }
                
                miTurno = !miTurno; // Cambiar turno
            }

            socket.close();
        } catch (Exception e) {
            System.out.println(Colores.X_CLR + "\n  Se ha perdido la conexión. " + e.getMessage() + Colores.RESET);
        }
        
        System.out.print("\nPresiona ENTER para retornar al Menú Principal...");
        scanner.nextLine(); scanner.nextLine(); 
    }

    private void procesarMovimiento(int index) {
        if (index >= 0 && index < 9 && !board[index].equals("X") && !board[index].equals("O")) {
            board[index] = isXTurn ? "X" : "O";

            if (checkWin()) {
                limpiarPantalla();
                imprimirTablero();
                Audio.playWinSound(); 
                String ganador = isXTurn ? "JUGADOR 1 (X)" : "JUGADOR 2 (O)";
                System.out.println("\n" + Colores.WIN_CLR + "  ¡FELICIDADES! " + ganador + " HA GANADO! " + Colores.RESET);
                if (isXTurn) this.scoreX++; else this.scoreO++;
                this.gameOver = true;
            } else if (isDraw()) {
                limpiarPantalla();
                imprimirTablero();
                System.out.println("\n" + Colores.GREY + "  ¡EMPATE! No quedan movimientos. " + Colores.RESET);
                this.gameOver = true;
            } else {
                this.isXTurn = !this.isXTurn; 
            }
        }
    }

    // ========== METODOS AUXILIARES Y DE ROBOT ==========
    private int leerEntradaNormal(Scanner scanner) {
        if (scanner.hasNextInt()) return scanner.nextInt() - 1;
        scanner.next(); return -1;
    }

    private int leerEntradaConLimite(Scanner scanner) {
        long tiempoInicio = System.currentTimeMillis();
        String entrada = "";
        boolean aTiempo = false;
        try {
            while (System.currentTimeMillis() - tiempoInicio < 10000) { 
                if (System.in.available() > 0) {
                    entrada = scanner.next();
                    aTiempo = true;
                    break;
                }
                Thread.sleep(100); 
            }
        } catch (Exception e) {}

        if (!aTiempo) {
            System.out.println("\n" + Colores.X_CLR + "  ¡TIEMPO AGOTADO! Se elegirá al azar." + Colores.RESET);
            try { Thread.sleep(2000); while(System.in.available() > 0) System.in.read(); } catch (Exception e) {} 
            return obtenerMovimientoAleatorio();
        }
        try { return Integer.parseInt(entrada) - 1; } catch (NumberFormatException e) { return -1; }
    }

    private int obtenerMovimientoAleatorio() {
        ArrayList<Integer> disp = new ArrayList<>();
        for (int i = 0; i < 9; i++) if (!board[i].equals("X") && !board[i].equals("O")) disp.add(i);
        if (disp.isEmpty()) return -1;
        return disp.get((int)(Math.random() * disp.size()));
    }

    private int buscarMovimientoGanador(String j) {
        int[][] lines = {{0,1,2}, {3,4,5}, {6,7,8}, {0,3,6}, {1,4,7}, {2,5,8}, {0,4,8}, {2,4,6}};
        for (int[] l : lines) {
            int c = 0, e = -1;
            for (int idx : l) {
                if (board[idx].equals(j)) c++;
                else if (!board[idx].equals("X") && !board[idx].equals("O")) e = idx;
            }
            if (c == 2 && e != -1) return e;
        }
        return -1;
    }

    private int obtenerMovimientoRobot() {
        if (dificultadRobot == 1) return obtenerMovimientoAleatorio();
        if (dificultadRobot == 2) {
            int ganar = buscarMovimientoGanador("O");
            return ganar != -1 ? ganar : obtenerMovimientoAleatorio();
        }
        if (dificultadRobot == 3) {
            int ganar = buscarMovimientoGanador("O"); if (ganar != -1) return ganar;
            int bloquear = buscarMovimientoGanador("X"); if (bloquear != -1) return bloquear;
            if (!board[4].equals("X") && !board[4].equals("O")) return 4;
            int[] esquinas = {0, 2, 6, 8};
            for (int e : esquinas) if (!board[e].equals("X") && !board[e].equals("O")) return e;
            return obtenerMovimientoAleatorio();
        }
        return obtenerMovimientoAleatorio();
    }

    public void imprimirTablero() {
        System.out.println("\n" + Colores.YELLOW + "       TABLERO EN CURSO " + Colores.RESET);
        System.out.println("          ---+---+---");
        for (int i = 0; i < 9; i += 3) {
            System.out.print("           ");
            for (int j = 0; j < 3; j++) {
                String cell = board[i + j];
                if (cell.equals("X")) System.out.print(Colores.X_CLR + cell + Colores.RESET);
                else if (cell.equals("O")) System.out.print(Colores.O_CLR + cell + Colores.RESET);
                else System.out.print(Colores.GREY + cell + Colores.RESET);
                if (j < 2) System.out.print(" | ");
            }
            System.out.println("\n          ---+---+---");
        }
    }

    public boolean checkWin() {
        int[][] lines = {{0,1,2}, {3,4,5}, {6,7,8}, {0,3,6}, {1,4,7}, {2,5,8}, {0,4,8}, {2,4,6}};
        for (int[] l : lines) if (board[l[0]].equals(board[l[1]]) && board[l[0]].equals(board[l[2]])) return true;
        return false;
    }

    public boolean isDraw() {
        for (int i = 0; i < 9; i++) if (!board[i].equals("X") && !board[i].equals("O")) return false;
        return true;
    }

    public void mostrarPuntuacion() {
        System.out.println("\n" + Colores.YELLOW + "=========================================" + Colores.RESET);
        System.out.println(Colores.YELLOW + "           MARCADOR GENERAL         " + Colores.RESET);
        System.out.println(Colores.YELLOW + "=========================================" + Colores.RESET);
        System.out.println("  " + Colores.X_CLR + "JUGADOR 1 (X):" + Colores.RESET + " " + this.scoreX);
        System.out.println("  " + Colores.O_CLR + "JUGADOR 2 (O):" + Colores.RESET + " " + this.scoreO);
        System.out.println(Colores.YELLOW + "=========================================" + Colores.RESET);
    }
}