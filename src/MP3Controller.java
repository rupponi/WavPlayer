public class MP3Controller {
    MP3Player mp3Player;
    Thread playerThread;
    boolean paused = false;
    long pauseTime = 0;


    public MP3Controller() {
        mp3Player = new MP3Player();
        playerThread = new Thread(mp3Player);
    }

    public void play() {
        if (!paused) {
            playerThread.start();
            mp3Player.stop();
        } else {
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

    public void resume() {
        playerThread.notify();

    }
}
