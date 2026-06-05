package trabajofinalmodulo;

import java.util.Scanner;

/**
 * Clase principal de arranque del sistema.
 * Coordina la interfaz de consola del Instituto Nacional de Sonzacate.
 */
public class main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Logica logica = new Logica();
        int opcion = 0;

       
        Audio.startBackgroundMusic();

        do {
            imprimirLogoASCII();

            System.out.println(Colores.YELLOW + "========================================" + Colores.RESET);
            System.out.println(Colores.YELLOW + "      TRES EN RAYA PROYECTO FINAL       " + Colores.RESET);
            System.out.println(Colores.YELLOW + "             MENU PRINCIPAL             " + Colores.RESET);
            System.out.println(Colores.YELLOW + "========================================" + Colores.RESET);
            System.out.println(" 1.  Iniciar Partida (Jugador vs Jugador)");
            System.out.println(" 2.  Iniciar Partida (Jugador vs Robot) ");
            System.out.println(" 3.  Ver Historial de Puntuaciones");
            System.out.println(" 4.  Configurar Sonido Retro (" + (Audio.musicOn ? Colores.WIN_CLR + "ENCENDIDO" : Colores.X_CLR + "APAGADO") + Colores.RESET + ")");
            System.out.println(" 5.  Ver Integrantes / Créditos");
            System.out.println(" 6.  Salir de la Aplicación");
            System.out.println(Colores.YELLOW + "----------------------------------------" + Colores.RESET);
            System.out.print("  Selecciona una opción del menú: ");

            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
                switch (opcion) {
                    case 1:
                        logica.iniciarJuego(scanner, false); // Falso = No usar Robot
                        break;
                    case 2:
                        logica.iniciarJuego(scanner, true);  // Verdadero = Jugar contra Robot
                        break;
                    case 3:
                        logica.mostrarPuntuacion();
                        System.out.print("\nPresiona ENTER para continuar...");
                        scanner.nextLine(); scanner.nextLine();
                        break;
                    case 4:
                        Audio.toggleMusic(null); 
                        System.out.println(Colores.WIN_CLR + "\n  Configuración guardada exitosamente." + Colores.RESET);
                        esperar(1000);
                        break;
                    case 5:
                        mostrarCreditos();
                        System.out.print("\nPresiona ENTER para continuar...");
                        scanner.nextLine(); scanner.nextLine();
                        break;
                    case 6:
                        System.out.println("\n" + Colores.WIN_CLR + "  Saliendo de forma segura. ¡Gracias por jugar!" + Colores.RESET);
                        break;
                    default:
                        System.out.println(Colores.X_CLR + "\n  Error: Opción fuera de rango (Elige de 1 a 6)." + Colores.RESET);
                        esperar(1500);
                }
            } else {
                System.out.println(Colores.X_CLR + "\n  Error: Carácter no admitido. Digita un número entero." + Colores.RESET);
                scanner.next(); 
                esperar(1500);
            }
        } while (opcion != 6);

        scanner.close();
    }

    private static void esperar(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }

    private static void imprimirLogoASCII() {
        System.out.println("\n" + Colores.O_CLR +
            " ████████╗██████╗ ███████╗███████╗    ███████╗███╗   ██╗    ██████╗  █████╗ ██╗   ██╗ █████╗ \n" +
            " ╚══██╔══╝██╔══██╗██╔════╝██╔════╝    ██╔════╝████╗  ██║    ██╔══██╗██╔══██╗╚██╗ ██╔╝██╔══██╗\n" +
            "    ██║   ██████╔╝█████╗  ███████╗    █████╗  ██╔██╗ ██║    ██████╔╝███████║ ╚████╔╝ ███████║\n" +
            "    ██║   ██╔══██╗██╔══╝  ╚════██║    ██╔══╝  ██║╚██╗██║    ██╔══██╗██╔══██║  ╚██╔╝  ██╔══██║\n" +
            "    ██║   ██║  ██║███████╗███████║    ███████╗██║ ╚████║    ██║  ██║██║  ██║   ██║   ██║  ██║\n" +
            "    ╚═╝   ╚═╝  ╚═╝╚══════╝╚══════╝    ╚══════╝╚═╝  ╚═══╝    ╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝   ╚═╝   ╚═╝" + Colores.RESET);
    }

    private static void mostrarCreditos() {
        System.out.println("\n" + Colores.O_CLR + "========================================" + Colores.RESET);
        System.out.println(Colores.O_CLR + "         DATOS DE LA ASIGNATURA         " + Colores.RESET);
        System.out.println(Colores.O_CLR + "========================================" + Colores.RESET);
        System.out.println("\n  " + Colores.YELLOW + "EQUIPO DESARROLLADOR:" + Colores.RESET);
        
        
        System.out.println("     " + Colores.TEXT_WHITE + "Andres Eduardo Marinero Cruz" + Colores.RESET);
        System.out.println("     " + Colores.TEXT_WHITE + "David Alejandro Escobar Cabrera" + Colores.RESET);
        System.out.println("     " + Colores.TEXT_WHITE + "Víctor Alexander Martínez Santacruz" + Colores.RESET);
        
        System.out.println("\n  " + Colores.GREY + "Sonzacate, Sonsonate - El Salvador, 2026" + Colores.RESET);
        System.out.println(Colores.O_CLR + "========================================" + Colores.RESET);
    }
}