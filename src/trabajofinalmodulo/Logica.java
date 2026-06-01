package trabajofinalmodulo;

import java.util.Scanner;
import java.util.ArrayList;

/**
 * Clase que gestiona el estado interno del juego, validación de turnos,
 * impresión dinámica del tablero y control de puntuaciones.
 */
public class Logica {
    public String[] board;       
    public boolean isXTurn;      
    public int scoreX;           
    public int scoreO;           
    public boolean gameOver;     
    public boolean contraRobot;  // Flag para identificar el modo de juego

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

    /**
     * Controla el bucle dinámico de la partida activa adaptándose al Robot o a un Humano.
     */
    public void iniciarJuego(Scanner scanner, boolean contraRobot) {
        this.contraRobot = contraRobot;
        this.resetGame();
        
        while (!gameOver) {
            imprimirTablero();
            int index = -1;

            // TURNO DEL JUGADOR 1 (SIEMPRE HUMANO)
            if (isXTurn) {
                String jugadorActual = Colores.X_CLR + "JUGADOR 1 (X)" + Colores.RESET;
                System.out.println("\n  Turno de: " + jugadorActual);
                System.out.print("  Elige una casilla disponible (1-9): ");

                if (scanner.hasNextInt()) {
                    int seleccion = scanner.nextInt();
                    index = seleccion - 1;
                } else {
                    System.out.println(Colores.X_CLR + "  Error: Debes ingresar exclusivamente números del 1 al 9." + Colores.RESET);
                    scanner.next(); 
                    continue; 
                }
            } 
            // TURNO DEL JUGADOR 2 (HUMANO O ROBOT)
            else {
                if (contraRobot) {
                    System.out.println("\n  Turno del ROBOT (O)... Pensando...");
                    try { Thread.sleep(800); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    index = obtenerMovimientoRobot();
                    System.out.println("  El Robot eligió la casilla: " + (index + 1));
                } else {
                    String jugadorActual = Colores.O_CLR + "JUGADOR 2 (O)" + Colores.RESET;
                    System.out.println("\n  Turno de: " + jugadorActual);
                    System.out.print("  Elige una casilla disponible (1-9): ");

                    if (scanner.hasNextInt()) {
                        int seleccion = scanner.nextInt();
                        index = seleccion - 1;
                    } else {
                        System.out.println(Colores.X_CLR + "  Error: Debes ingresar exclusivamente números del 1 al 9." + Colores.RESET);
                        scanner.next(); 
                        continue;
                    }
                }
            }

            // Validación de límites del arreglo y disponibilidad de la casilla elegida
            if (index >= 0 && index < 9 && !board[index].equals("X") && !board[index].equals("O")) {
                board[index] = isXTurn ? "X" : "O";
                Audio.playTone(isXTurn ? 440 : 554, 100); 

                if (checkWin()) {
                    imprimirTablero();
                    String ganador = isXTurn ? "JUGADOR 1 (X)" : (contraRobot ? "EL ROBOT (O)" : "JUGADOR 2 (O)");
                    System.out.println("\n" + Colores.WIN_CLR + "  ¡FELICIDADES! " + ganador + " HA GANADO LA PARTIDA! " + Colores.RESET);
                    if (isXTurn) this.scoreX++; else this.scoreO++;
                    Audio.playWinSound();
                    this.gameOver = true;
                } else if (isDraw()) {
                    imprimirTablero();
                    System.out.println("\n" + Colores.GREY + "  ¡EMPATE! No quedan movimientos disponibles en el tablero. " + Colores.RESET);
                    this.gameOver = true;
                } else {
                    this.isXTurn = !this.isXTurn; 
                }
            } else {
                if (!(!isXTurn && contraRobot)) { // Evita spamear error si fuese un fallo lógico del bot
                    System.out.println(Colores.X_CLR + "  Error: Casilla inválida o ya ocupada. Intenta de nuevo." + Colores.RESET);
                }
            }
        }
        
        System.out.print("\nPresiona ENTER para retornar al Menú Principal...");
        scanner.nextLine(); 
        scanner.nextLine(); 
    }

    /**
     * Inteligencia artificial de toma de decisiones para el Robot:
     * 1. Revisa si puede ganar en este turno.
     * 2. Bloquea al jugador Humano si está por ganar.
     * 3. Prioriza el centro del tablero.
     * 4. Escoge una opción aleatoria válida.
     */
    private int obtenerMovimientoRobot() {
        int[][] lines = new int[][]{
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, 
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, 
            {0, 4, 8}, {2, 4, 6}             
        };

        // 1. ¿Puede ganar el robot (O)?
        for (int[] l : lines) {
            int countO = 0, emptyIndex = -1;
            for (int idx : l) {
                if (board[idx].equals("O")) countO++;
                else if (!board[idx].equals("X")) emptyIndex = idx;
            }
            if (countO == 2 && emptyIndex != -1) return emptyIndex;
        }

        // 2. ¿Debe bloquear al jugador (X)?
        for (int[] l : lines) {
            int countX = 0, emptyIndex = -1;
            for (int idx : l) {
                if (board[idx].equals("X")) countX++;
                else if (!board[idx].equals("O")) emptyIndex = idx;
            }
            if (countX == 2 && emptyIndex != -1) return emptyIndex;
        }

        // 3. Tomar el centro por estrategia si está disponible
        if (!board[4].equals("X") && !board[4].equals("O")) {
            return 4;
        }

        // 4. Jugar de forma aleatoria en los espacios libres
        ArrayList<Integer> disponibles = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (!board[i].equals("X") && !board[i].equals("O")) {
                disponibles.add(i);
            }
        }
        int randIdx = (int) (Math.random() * disponibles.size());
        return disponibles.get(randIdx);
    }

    public void imprimirTablero() {
        System.out.println("\n" + Colores.YELLOW + "       TABLERO EN CURSO " + Colores.RESET);
        System.out.println("          ---+---+---");
        for (int i = 0; i < 9; i += 3) {
            System.out.print("           ");
            for (int j = 0; j < 3; j++) {
                String cell = board[i + j];
                if (cell.equals("X")) {
                    System.out.print(Colores.X_CLR + cell + Colores.RESET);
                } else if (cell.equals("O")) {
                    System.out.print(Colores.O_CLR + cell + Colores.RESET);
                } else {
                    System.out.print(Colores.GREY + cell + Colores.RESET);
                }
                if (j < 2) System.out.print(" | ");
            }
            System.out.println();
            System.out.println("          ---+---+---");
        }
    }

    public boolean checkWin() {
        int[][] lines = new int[][]{
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, 
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, 
            {0, 4, 8}, {2, 4, 6}             
        };
        for (int[] l : lines) {
            if (board[l[0]].equals(board[l[1]]) && board[l[0]].equals(board[l[2]])) {
                return true;
            }
        }
        return false;
    }

    public boolean isDraw() {
        for (int i = 0; i < 9; i++) {
            if (!board[i].equals("X") && !board[i].equals("O")) {
                return false;
            }
        }
        return true;
    }

    public void mostrarPuntuacion() {
        System.out.println("\n" + Colores.YELLOW + "=========================================" + Colores.RESET);
        System.out.println(Colores.YELLOW + "           MARCADOR GENERAL         " + Colores.RESET);
        System.out.println(Colores.YELLOW + "=========================================" + Colores.RESET);
        System.out.println("  " + Colores.X_CLR + "JUGADOR 1 (X):" + Colores.RESET + " " + this.scoreX + " Partidas Ganadas");
        System.out.println("  " + Colores.O_CLR + "JUGADOR 2 / ROBOT (O):" + Colores.RESET + " " + this.scoreO + " Partidas Ganadas");
        System.out.println(Colores.YELLOW + "=========================================" + Colores.RESET);
    }
}