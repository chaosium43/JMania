import javax.sound.sampled.*;
//import java.applet.AudioClip;
import java.io.*;
import java.util.HashMap;

//helper class with playing audio
public class AudioMaster implements LineListener {
    private HashMap<String, Clip> loadedClips = new HashMap<String,Clip>();

    public AudioMaster() { //an attempt was made at pre-loading the audio files
        File audioFolder = new File("audio");
        for (File f: audioFolder.listFiles()) {
            loadAudio(f);
        }
    }

    public void loadAudio(File audioFile) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            DataLine.Info info = new DataLine.Info(Clip.class, audioStream.getFormat());
            Clip audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.addLineListener(this);
            audioClip.open(audioStream);

            loadedClips.put(audioFile.getName(), audioClip);
        }
        catch (Exception e) {
            System.out.printf("UNABLE TO LOAD AUDIO: %s\n", e.getMessage());
        }
    }

    public void startAudio(String clipName) {
        Clip currentClip = loadedClips.get(clipName);
        currentClip.setFramePosition(0);
        loadedClips.get(clipName).start();
    }

    public void stopAudio(String clipName) {
        loadedClips.get(clipName).stop();
    }

    public void update(LineEvent event) {}
}
