package music.onestream.musicgetter;

/**
 * Created by ruspe_000 on 2017-02-03.
 */

public class MusicGetterHandler {

    private SpotifyMusicGetter spotifyMusicGetter;
    private LocalMusicGetter localMusicGetter;
    private GoogleMusicGetter googleMusicGetter;

    public MusicGetterHandler()
    {

    }

    public void setSpotifyMusicGetter(SpotifyMusicGetter spotifyMusicGetter) {
        this.spotifyMusicGetter = spotifyMusicGetter;
    }

    public void setGoogleMusicGetter(GoogleMusicGetter googleMusicGetter) {
        this.googleMusicGetter = googleMusicGetter;
    }

    public void setLocalMusicGetter(LocalMusicGetter localMusicGetter) {
        this.localMusicGetter = localMusicGetter;
    }

    public GoogleMusicGetter getGoogleMusicGetter() {
        return googleMusicGetter;
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
        if (googleMusicGetter != null) {
            googleMusicGetter.init();
        }
    }

    public void initAll()
    {
        initLocalMusicGetter();
        initSpotifyMusicGetter();
        initGoogleMusicGetter();
    }
}
