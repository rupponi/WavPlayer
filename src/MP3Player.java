import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.control.Slider;

import javafx.geometry.Insets;
import java.nio.file.Path;
import java.io.File;

import javax.media.Player;

import java.net.URL;

import javax.sound.sampled.AudioFileFormat;


public class MP3Player implements Runnable {

    private File songFile;
    private Path songPath;
    private URL songURL;
    private Player songPlayer;
    private AudioFileFormat songData;
    private volatile boolean status;//Thread status monitor.

    private long startingTime,currentTime,pastTime,songTime;
    private float songFrames,frameSpeed;
    private String output;
    private TextArea timer;
    private Slider timeSlider;

    private boolean resetNeeded;



    protected MP3Player() {
        status = true;
        resetNeeded = false;

        startingTime = 0;
        currentTime = 0;
        pastTime = 0;
        output = new String();

        timer = new TextArea();
        timer.setMaxHeight(25);
        timer.setMaxWidth(80);
        timer.setWrapText(true);
        timer.setEditable(false);
        timer.setFont(Font.font("Times New Roman", 12.0));

        timeSlider = new Slider();
        timeSlider.setMinSize(500,50);
        timeSlider.setMin(0);
        timeSlider.setMajorTickUnit(60);
        timeSlider.setBlockIncrement(1);
        timeSlider.setStyle("-fx-control-inner-background: #00ecff");


    }


    public Player getSongPlayer() {
        return songPlayer;
    }
    public void setSongPlayer(Player newPlayer) {
        songPlayer = newPlayer;
    }


    public Path getSongPath() {
        return songPath;
    }
    public void setSongPath(Path newSongPath) {
        songPath = newSongPath;
    }


    public URL getSongURL() {
        return songURL;
    }
    public void setSongURL(URL newSongURL) {
        songURL = newSongURL;
    }


    public File getSongFile() {
        return songFile;
    }
    public void setSongFile(File newSong) {
        songFile = newSong;
    }


    public AudioFileFormat getSongData() {
        return songData;
    }
    public void setSongData(AudioFileFormat newSongData) {
        songData = newSongData;
    }

    public float getSongFrames() {
        return songFrames;
    }
    public void setSongFrames(float newSongFrames) {
        songFrames = newSongFrames;
    }


    public float getFrameSpeed() {
        return frameSpeed;
    }
    public void setFrameSpeed(float newFrameSpeed) {
        frameSpeed = newFrameSpeed;
    }


    public long getSongTime() {
        return songTime;
    }
    public void setSongTime(long newSongTime) {
        songTime = newSongTime;
    }


    public long getStartingTime() {
        return startingTime;
    }
    public void setStartingTime(long newStartingTime) {
        startingTime = newStartingTime;
    }


    public long getCurrentTime() {
        return currentTime;
    }
    public void setCurrentTime(long newCurrentTime) {
        currentTime = newCurrentTime;
    }


    public TextArea getTimer() {
        return timer;
    }
    public void setTimer(TextArea newTimer) {
        timer = newTimer;
    }


    public Slider getTimeSlider() {
        return timeSlider;
    }
    public void setTimeSlider(Slider newTimeSlider) {
        timeSlider = newTimeSlider;
    }


    public void resetPlayer() {
        resetNeeded = true;
    }

    public void run() {//Run method simply begins stream. Stopping this will restart the song, similar to stop button in music player.
        startingTime = System.currentTimeMillis()/1000;
        songPlayer.start();

        while((System.currentTimeMillis()/1000) < startingTime + songTime) {
            pastTime = currentTime;

            currentTime = (System.currentTimeMillis()/1000)-startingTime;
            if (currentTime != pastTime) {
                output = String.format("%d:%02d/%d:%02d",currentTime/60,currentTime-(currentTime/60)*60, songTime/60,songTime-(songTime/60)*60);
                timer.setText(output);
                if (resetNeeded) {
                    startingTime = System.currentTimeMillis()/1000;
                    resetNeeded = false;
                }
                timeSlider.setValue(currentTime);
            }
        }
        status = false;

        System.out.println("Song completed.");
        if (!status) {
            songPlayer.stop();
        }
    }

    public void stop() {
        status = false;
    }
}
