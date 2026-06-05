package trabajofinalmodulo;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class Audio {
    public static boolean musicOn = false;
    private static Thread musicThread;
    private static volatile boolean musicRunning = false;
    private static final Audio instancia = new Audio(); // Instancia interna para hilos

    public boolean isMusicOn() { 
        return musicOn; 
    }

    public static void playTone(int freq, int ms) {
        new Thread(() -> {
            try {
                float sr = 44100f;
                int n = (int)(sr * ms / 1000);
                byte[] d = new byte[n];
                
                if (freq <= 20) {
                    for (int i = 0; i < n; i++) d[i] = 0;
                } else {
                    for (int i = 0; i < n; i++) {
                        double env = 1.0 - (double) i / n;
                        double wave = Math.signum(Math.sin(2 * Math.PI * i * freq / sr));
                        d[i] = (byte)(wave * 25 * env);
                    }
                }
                AudioFormat fmt = new AudioFormat(sr, 8, 1, true, true);
                SourceDataLine l = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, fmt));
                l.open(fmt); l.start(); l.write(d, 0, d.length); l.drain(); l.close();
            } catch (Exception ignored) {}
        }).start();
    }

    public static void playWinSound() {
        new Thread(() -> {
            int[] notes = {523, 659, 784, 1047};
            for (int note : notes) {
                playTone(note, 90);
                try { Thread.sleep(100); } catch (InterruptedException e) { break; }
            }
        }).start();
    }

    // Método que llamaba Main.java originalmente
    public static void startBackgroundMusic() {
        if (!musicOn) {
            toggleMusic(null);
        }
    }

    public static void toggleMusic(Runnable onUpdateBtn) {
        musicOn = !musicOn;
        if (onUpdateBtn != null) onUpdateBtn.run();
        
        if (musicOn) {
            musicRunning = true;
            musicThread = new Thread(() -> {
                // Melodía calmada y relajada (ideal para pensar)
                // Formato: {Frecuencia en Hz, Duración en ms}
                int[][] calmLoop = {
                    {261, 500}, {329, 500}, {392, 500}, {329, 500}, // Acorde de Do Mayor (C)
                    {349, 500}, {440, 500}, {523, 500}, {440, 500}, // Acorde de Fa Mayor (F)
                    {392, 500}, {493, 500}, {587, 500}, {493, 500}, // Acorde de Sol Mayor (G)
                    {261, 1000}, {1, 500}                           // Resolución larga y pausa
                };
                
                int step = 0;
                while (musicRunning) {
                    int[] note = calmLoop[step % calmLoop.length];
                    playTone(note[0], note[1] - 15);
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
}