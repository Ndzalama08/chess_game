package chess.util;

import javax.sound.sampled.*;
import java.io.IOException;

public class SoundManager {
    private static boolean enabled = true;

    public static void setEnabled(boolean on) {
        enabled = on;
    }

    /** Play a WAV file from resources/sounds/*.wav */
    public static void play(String resourcePath) {
        if (!enabled) return;
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(
                SoundManager.class.getResourceAsStream(resourcePath))) {
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }
}
