import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncodingAttributes;

import javax.media.Manager;
import javax.media.Player;
import java.io.*;
import java.net.URL;

public class MP3Converter {

    Player testPlayer;
    URL testURL;
    File convertedWav;

    public void convertToWav(File input) {//Adjusts incoming mp3 formats to a pcm_s16le codec of 8 bit audio in dual channels. 44100 Hz was optimal for balancing conversion performance and sound quality.

        File output = new File("output");
        AudioAttributes outputProperties = new AudioAttributes();
        outputProperties.setCodec("pcm_s16le");
        outputProperties.setBitRate(16);
        outputProperties.setChannels(2);
        outputProperties.setSamplingRate(44100);//44.1 kHz should be the optimal sampling rate. NOTE: Increasing could proportionally worsen runtime of loading songs of mp3 format in particular.


        EncodingAttributes encodingProperties = new EncodingAttributes();
        encodingProperties.setFormat("wav");
        encodingProperties.setAudioAttributes(outputProperties);

        Encoder encoder = new Encoder();

        try {
            encoder.encode(input,output,encodingProperties);
            convertedWav = output;
            testURL = output.toURI().toURL();
            testPlayer = Manager.createRealizedPlayer(testURL);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}