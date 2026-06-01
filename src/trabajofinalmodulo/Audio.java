<<<<<<< HEAD
package src.trabajofinalmodulo;

trabajofinalmodulo

public class Audio {
    private boolean musicOn = false;
    private Thread musicThread;
    private volatile boolean musicRunning = false;

    public boolean isMusicOn() { 
        return musicOn; 
    }

    public void playTone(int freq, int ms) {
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

    public void playWinSound() {
        new Thread(() -> {
            int[] notes = {523, 659, 784, 1047};
            for (int note : notes) {
                playTone(note, 90);
                try { Thread.sleep(100); } catch (InterruptedException e) { break; }
            }
        }).start();
    }

    public void toggleMusic(Runnable onUpdateBtn) {
        musicOn = !musicOn;
        if (onUpdateBtn != null) onUpdateBtn.run();
        
        if (musicOn) {
            musicRunning = true;
            musicThread = new Thread(() -> {
                int[][] retroLoop = {
                    {523, 160}, {659, 160}, {784, 160}, {659, 160},
                    {587, 160}, {698, 160}, {880, 160}, {698, 160},
                    {659, 160}, {784, 160}, {1047, 160}, {784, 160},
                    {587, 320}, {494, 320},
                    {523, 160}, {659, 160}, {784, 160}, {659, 160},
                    {880, 320}, {784, 320}, {659, 320}, {523, 160},
                    {587, 160}, {494, 160}, {440, 320}, {1, 160}
                };
                int step = 0;
                while (musicRunning) {
                    int[] note = retroLoop[step % retroLoop.length];
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
=======
>>>>>>> 203bc4f92d0988429bce8c92c0bf4a3ecabebac4
