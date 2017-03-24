package music.onestream.musicgetter;

import android.content.Context;

import java.util.ArrayList;
import java.util.TreeMap;

import music.onestream.util.AsyncResponse;

/**
 * Created by ruspe_000 on 2017-02-03.
 */

public class MusicGetterHandler {

    private AsyncResponse context;
    private SpotifyMusicGetter spotifyMusicGetter;
    private TreeMap<String, LocalMusicGetter> localMusicGetters;
    private GoogleMusicGetter googleMusicGetter;

    public MusicGetterHandler(AsyncResponse returnLocation)
    {
        this.context = returnLocation;
    }

    public void addSpotifyMusicGetter(SpotifyMusicGetter spotifyMusicGetter) {
        this.spotifyMusicGetter = spotifyMusicGetter;
    }

    public void addGoogleMusicGetter(GoogleMusicGetter googleMusicGetter) {
        this.googleMusicGetter = googleMusicGetter;
    }

    public void addLocalMusicGetter(LocalMusicGetter localMusicGetter, String directory) {
        if (localMusicGetters == null)
        {
            localMusicGetters = new TreeMap<String, LocalMusicGetter>();
        }
        this.localMusicGetters.put(directory, localMusicGetter);
    }

    public GoogleMusicGetter getGoogleMusicGetter() {
        return googleMusicGetter;
    }

    public TreeMap getLocalMusicGetter() {
        return localMusicGetters;
    }

    public SpotifyMusicGetter getSpotifyMusicGetter() {
        return spotifyMusicGetter;
    }

    public void initLocalMusicGetter(String directory) {
        LocalMusicGetter localMusicGetter = localMusicGetters.get(directory);
        if (localMusicGetter != null) {
            localMusicGetter.SAR = context;
            localMusicGetter.execute();

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

}
