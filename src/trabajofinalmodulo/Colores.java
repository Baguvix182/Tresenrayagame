package trabajofinalmodulo;

import java.awt.Color;

public class Colores {

    // --- COLORES PARA LA TERMINAL (ANSI CODES) ---
    public static final String RESET = "\u001B[0m";
    public static final String YELLOW = "\u001B[93m";       
    public static final String X_CLR = "\u001B[96m";        
    public static final String O_CLR = "\u001B[95m";        
    public static final String WIN_CLR = "\u001B[92m";      
    public static final String GREY = "\u001B[90m";         
    public static final String TEXT_WHITE = "\u001B[97m";   

    // --- TEMA "DRÁCULA" PARA ENTORNO GRÁFICO (AWT/SWING) ---
    // Tonos nocturnos con acentos pastel (Muy relajante para la vista)
    
    public static final Color UI_BACKGROUND = new Color(40, 42, 54);       // Fondo principal oscuro
    public static final Color UI_CELL_BACKGROUND = new Color(68, 71, 90);  // Fondo de las celdas (Gris azulado)
    public static final Color UI_GRID_COLOR = new Color(98, 114, 164);     // Cuadrícula (Morado/Grisáceo sutil)
    
    public static final Color UI_PLAYER_X = new Color(139, 233, 253);      // Cyan pastel vibrante para la X
    public static final Color UI_PLAYER_O = new Color(255, 121, 198);      // Rosa chicle pastel para la O
    
    public static final Color UI_WIN_COLOR = new Color(80, 250, 123);      // Verde lima pastel para victoria
    
    public static final Color UI_AVATAR_X = new Color(139, 233, 253);
    public static final Color UI_AVATAR_O = new Color(255, 121, 198);
    
    public static final Color UI_TEXT_WHITE = new Color(248, 248, 242);    // Blanco humo (Cero fatiga visual)
    public static final Color UI_TEXT_GRAY  = new Color(98, 114, 164);     // Texto secundario a juego con la cuadrícula
}