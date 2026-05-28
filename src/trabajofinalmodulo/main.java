package trabajofinalmodulo;

public class main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("🎮 PROBANDO MOTOR: TRES EN RAYA DE LOS 3 COMPAS");
        System.out.println("=================================================");

        // 1. PROBANDO LA PALETA DE COLORES
        System.out.println("\n[1] Cargando configuración estética...");
        // Como las variables en Colores son 'static final', no necesitas hacer "new"
        System.out.println("-> Color de Fondo (BG) cargado: " + Colores.BG);
        System.out.println("-> Color del jugador X: " + Colores.X_CLR);
        System.out.println("-> Color del jugador O: " + Colores.O_CLR);
        System.out.println("✅ Estructura visual lista.");


        // 2. PROBANDO EL AUDIO SINTÉTICO (MÚSICA RETRO EN BUCLE)
        System.out.println("\n[2] Encendiendo el sistema de audio...");
        Audio sistemaAudio = new Audio();
        
        // Encendemos la música retro de fondo
        System.out.println("🎵 Reproduciendo música loop de fondo...");
        sistemaAudio.toggleMusic(() -> {
            System.out.println("-> El botón de música cambió de estado de manera segura.");
        });


        // 3. PROBANDO LA LÓGICA DEL JUEGO
        System.out.println("\n[3] Inicializando tablero y simulando partida...");
        Logica juego = new Logica();
        
        // Obtenemos el arreglo del tablero
        String[] tablero = juego.getBoard();
        
        // Simulamos que el jugador X gana en la primera fila (casillas 0, 1 y 2)
        tablero[0] = "X";
        tablero[1] = "X";
        tablero[2] = "X";
        
        System.out.println("-> Turno actual inicial: " + juego.getCurrent());
        System.out.println("-> Analizando si hay un ganador en el tablero simulado...");
        
        int[] lineaGanadora = juego.checkWinner();
        
        if (lineaGanadora != null) {
            System.out.println("🏆 ¡Hay un ganador! Línea completada en casillas: " 
                                + lineaGanadora[0] + ", " + lineaGanadora[1] + ", " + lineaGanadora[2]);
            
            // Si hay ganador, disparamos el efecto de victoria de la clase Audio
            System.out.println("🔊 Reproduciendo efecto de victoria (Chiptune)...");
            sistemaAudio.playWinSound();
            
            // Sumamos el puntaje correspondiente
            juego.incrementScoreX();
            System.out.println("-> Puntuación actualizada -> X: " + juego.getScoreX() + " | O: " + juego.getScoreO());
        } else {
            System.out.println("❌ No hay ganadores en esta jugada.");
        }

        System.out.println("\n=================================================");
        System.out.println("🏁 FIN DEL TEST - Las 3 clases funcionan unidas");
        System.out.println("=================================================");
        
        // Mantenemos el programa vivo un momento para alcanzar a oír la música retro
        try {
            Thread.sleep(6000); // Suena durante 6 segundos y luego se apaga el test
            System.out.println("\n👋 Apagando música de fondo y cerrando prueba.");
            sistemaAudio.toggleMusic(null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}