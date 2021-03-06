package player;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import sources.Track;

import java.io.File;
import java.util.Observable;

public class Player extends Observable implements Runnable {

    private static final Player instance = new Player();
    private MediaPlayer mediaPlayer;
    private Track track = null;
    private double volume;
    private int actualPlaylist = 0;

    private Player() {
        volume = 0.5;
    }

    public void play(int actualPlaylist, Track track, boolean buttonAndDoubleClick) {
        if (this.track == null) {
            this.track = track;
            this.actualPlaylist = actualPlaylist;
            Media hit = new Media(new File(track.getPath()).toURI().toString());
            mediaPlayer = new MediaPlayer(hit);
            mediaPlayer.setVolume(volume);
            mediaPlayer.play();
        } else if (this.track.equals(track) && this.actualPlaylist == actualPlaylist && buttonAndDoubleClick) {
            if (!isPlaying()) {
                mediaPlayer.play();
            } else if (hasEnded()) {
                mediaPlayer.seek(Duration.ZERO);
            } else {
                mediaPlayer.pause();
            }
        } else {
            this.track = track;
            this.actualPlaylist = actualPlaylist;
            if (isPlaying()) {
                mediaPlayer.stop();
            }
            Media hit = new Media(new File(track.getPath()).toURI().toString());
            mediaPlayer = new MediaPlayer(hit);
            mediaPlayer.setVolume(volume);
            if (!(isPlaying())) {
                mediaPlayer.play();
            } else {
                mediaPlayer.pause();
            }
        }
    }

    public Track getTrack() {
        return track;
    }

    public double getVolume() {
        return volume;
    }

    public void setTrack(Track track) {
        this.track = track;
        Media hit = new Media(new File(track.getPath()).toURI().toString());
        mediaPlayer = new MediaPlayer(hit);
    }

    public void setActualPlaylist(int actualPlaylist) {
        this.actualPlaylist = actualPlaylist;
    }

    public int getActualPlaylist() {
        return actualPlaylist;
    }

    public boolean isPlaying() {
        if (mediaPlayer == null) {
            return false;
        }
        Status status = mediaPlayer.getStatus();
        return status == Status.PLAYING;
    }

    public boolean hasEnded() {
        return mediaPlayer.getCurrentTime().equals(mediaPlayer.getStopTime());
    }

    public static Player getInstance() {
        return instance;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getCurrentTime() {
        return mediaPlayer.getCurrentTime().toMillis();
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (hasEnded()) {
                    setChanged();
                    notifyObservers();
                }
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }
}
