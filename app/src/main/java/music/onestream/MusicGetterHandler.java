package music.onestream;

import java.util.ArrayList;

/**
 * Created by ruspe_000 on 2017-02-03.
 */

public class MusicGetterHandler {

    private SpotifyMusicGetter spotifyMusicGetter;
    private LocalMusicGetter localMusicGetter;
    private GoogleMusicMusicGetter googleMusicMusicGetter;

    public MusicGetterHandler()
    {

    }

    public void setSpotifyMusicGetter(SpotifyMusicGetter spotifyMusicGetter) {
        this.spotifyMusicGetter = spotifyMusicGetter;
    }

    public void setGoogleMusicMusicGetter(GoogleMusicMusicGetter googleMusicMusicGetter) {
        this.googleMusicMusicGetter = googleMusicMusicGetter;
    }

    public void setLocalMusicGetter(LocalMusicGetter localMusicGetter) {
        this.localMusicGetter = localMusicGetter;
    }

    public GoogleMusicMusicGetter getGoogleMusicMusicGetter() {
        return googleMusicMusicGetter;
    }

    public LocalMusicGetter getLocalMusicGetter() {
        return localMusicGetter;
    }

    public SpotifyMusicGetter getSpotifyMusicGetter() {
        return spotifyMusicGetter;
    }

    public void initLocalMusicGetter() {
        if (localMusicGetter != null) {
            localMusicGetter.init();
        }
    }

    public void initSpotifyMusicGetter() {
        if (spotifyMusicGetter != null) {
            spotifyMusicGetter.init();
        }
    }

    public void initGoogleMusicGetter() {
        if (googleMusicMusicGetter != null) {
            googleMusicMusicGetter.init();
        }
    }

    public void initAll()
    {
        initLocalMusicGetter();
        initSpotifyMusicGetter();
        initGoogleMusicGetter();
    }
}
