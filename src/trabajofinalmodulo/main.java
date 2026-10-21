package trabajofinalmodulo;

import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Logica logica = new Logica();
        int opcion = 0;

        Audio.startBackgroundMusic(); 

        do {
            logica.limpiarPantalla(); 
            imprimirLogoASCII();

            System.out.println(Colores.YELLOW + "========================================" + Colores.RESET);
            System.out.println(Colores.YELLOW + "             MENU PRINCIPAL             " + Colores.RESET);
            System.out.println(Colores.YELLOW + "========================================" + Colores.RESET);
            System.out.println(" 1.  Iniciar Partida (Jugador vs Jugador Local)");
            System.out.println(" 2.  Iniciar Partida (Jugador vs Robot) ");
            System.out.println(" 3.  Ver Historial de Puntuaciones");
            System.out.println(" 4.  Configurar Sonido Retro");
            System.out.println(" 5.  Ver Integrantes / Créditos");
            System.out.println(" 6.  Salir de la Aplicación");
            System.out.println(Colores.WIN_CLR + " --- MULTIJUGADOR ONLINE / RED ---" + Colores.RESET);
            System.out.println(" 7.  Crear sala en red (Ser Servidor)");
            System.out.println(" 8.  Unirse a sala en red (Ser Cliente)");
            System.out.println(Colores.YELLOW + "----------------------------------------" + Colores.RESET);
            System.out.print("  Selecciona una opción del menú: ");

            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
                switch (opcion) {
                    case 1:
                        logica.iniciarJuego(scanner, false, 0); 
                        break;
                    case 2:
                        int dif = elegirDificultad(scanner);
                        if (dif != -1) logica.iniciarJuego(scanner, true, dif);  
                        break;
                    case 3:
                        logica.mostrarPuntuacion();
                        System.out.print("\nPresiona ENTER para continuar...");
                        scanner.nextLine(); scanner.nextLine();
                        break;
                    case 4:
                        Audio.toggleMusic(null);
                        String estado = Audio.musicOn ? "ENCENDIDA" : "APAGADA";
                        System.out.println(Colores.WIN_CLR + "\n  Música Retro: " + estado + Colores.RESET);
                        esperar(1500);
                        break;
                    case 5:
                        mostrarCreditos();
                        System.out.print("\nPresiona ENTER para continuar...");
                        scanner.nextLine(); scanner.nextLine();
                        break;
                    case 6:
                        System.out.println("\n" + Colores.WIN_CLR + "  Saliendo de forma segura. ¡Gracias por jugar!" + Colores.RESET);
                        break;
                    case 7:
                        // CORRECCIÓN APLICADA AQUÍ
                        logica.iniciarJuegoRed(scanner, true, ""); 
                        break;
                    case 8:
                        // CORRECCIÓN APLICADA AQUÍ
                        System.out.print("\n  Ingresa la IP del servidor (ej. 192.168.1.5 o localhost): ");
                        String ip = scanner.next();
                        logica.iniciarJuegoRed(scanner, false, ip); 
                        break;
                    default:
                        System.out.println(Colores.X_CLR + "\n  Error: Opción fuera de rango." + Colores.RESET);
                        esperar(1500);
                }
            } else {
                System.out.println(Colores.X_CLR + "\n  Error: Carácter no admitido." + Colores.RESET);
                scanner.next(); 
                esperar(1500);
            }
        } while (opcion != 6);

        scanner.close();
        System.exit(0);
    }

    private static int elegirDificultad(Scanner scanner) {
        System.out.println("\n" + Colores.YELLOW + "--- SELECCIONA LA DIFICULTAD ---" + Colores.RESET);
        System.out.println(" 1. Fácil\n 2. Normal\n 3. Difícil\n 4. Cancelar");
        System.out.print(" Elige una opción: ");
        if (scanner.hasNextInt()) {
            int dif = scanner.nextInt();
            if (dif >= 1 && dif <= 3) return dif;
            if (dif == 4) return -1;
        } else { scanner.next(); }
        return -1;
    }

    private static void esperar(int ms) { try { Thread.sleep(ms); } catch (InterruptedException e) {} }

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
        System.out.println("  " + Colores.YELLOW + "EQUIPO DESARROLLADOR:" + Colores.RESET);
        System.out.println("     " + Colores.TEXT_WHITE + "Andres Eduardo Marinero Cruz" + Colores.RESET);
        System.out.println("     " + Colores.TEXT_WHITE + "David Alejandro Escobar Cabrera" + Colores.RESET);
        System.out.println("     " + Colores.TEXT_WHITE + "Víctor Alexander Martínez Santacruz" + Colores.RESET);
        System.out.println(Colores.O_CLR + "========================================" + Colores.RESET);
    }
}