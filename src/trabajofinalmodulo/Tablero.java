package trabajofinalmodulo;

/**
 * Se encarga exclusivamente del diseño y la proyección visual del tablero 3x3 en consola.
 */
public class Tablero {

    /**
     * Dibuja la cuadrícula activa aplicando los colores correspondientes.
     */
    public void imprimir(String[] board) {
        System.out.println("\n" + Colores.YELLOW + "      🥊 TABLERO EN CURSO 🥊" + Colores.RESET);
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
}