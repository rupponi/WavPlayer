import javax.media.Time;

public class MP3Controller {
    MP3Player mp3Player;
    Thread playerThread;
    boolean paused = false;
    long pauseTime = 0;
    long rewindTime = 0;
    long forwardTime = 0;


    public MP3Controller() {
        mp3Player = new MP3Player();
        playerThread = new Thread(mp3Player);
    }

    public void play() {
        if (!paused) {
            playerThread.start();
            mp3Player.stop();
        }
        else {
            mp3Player.setStartingTime(System.currentTimeMillis()/1000 - pauseTime);
            playerThread.resume();
            mp3Player.getSongPlayer().start();
        }
    }

    public void pause() {
        playerThread.suspend();
        paused = true;
        pauseTime = mp3Player.getCurrentTime();
        mp3Player.getSongPlayer().stop();
    }

    public void rewind() {
        playerThread.suspend();
        rewindTime = mp3Player.getCurrentTime()- 10;
        mp3Player.stop();
        mp3Player.getSongPlayer().stop();

        if (rewindTime > 0) {
            mp3Player.setStartingTime(System.currentTimeMillis() / 1000 - rewindTime);
            mp3Player.getSongPlayer().setMediaTime(new Time(rewindTime * 1000000000));
            playerThread.resume();
            mp3Player.getSongPlayer().start();
        }
        else {
            mp3Player.setStartingTime(0);
            mp3Player.getSongPlayer().setMediaTime(new Time(0));
            playerThread.resume();
            mp3Player.getSongPlayer().start();
        }
    }

    public void fastForward() {
        playerThread.suspend();
        forwardTime = mp3Player.getCurrentTime() + 10;
        mp3Player.stop();
        mp3Player.getSongPlayer().stop();

        if (forwardTime < mp3Player.getSongTime()) {
            mp3Player.setStartingTime(System.currentTimeMillis() / 1000 - forwardTime);
            mp3Player.getSongPlayer().setMediaTime(new Time(forwardTime * 1000000000));
            playerThread.resume();
            mp3Player.getSongPlayer().start();
        }
        else {
            mp3Player.setStartingTime(System.currentTimeMillis() / 1000 - 1);
            mp3Player.getSongPlayer().setMediaTime(new Time((mp3Player.getSongTime()-1) * 1000000000));
        }
    }
}
