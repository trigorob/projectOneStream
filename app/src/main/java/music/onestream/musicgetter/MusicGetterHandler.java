package music.onestream.musicgetter;

import java.util.TreeMap;

import music.onestream.util.AsyncResponse;

/**
 * Created by ruspe_000 on 2017-02-03.
 */

public class MusicGetterHandler {

    private AsyncResponse context;
    private SpotifyMusicGetter spotifyMusicGetter;
    private TreeMap<String, LocalMusicGetter> localMusicGetters;
    private SoundCloudMusicGetter soundCloudMusicGetter;

    public MusicGetterHandler(AsyncResponse returnLocation)
    {
        this.context = returnLocation;
    }

    public void freeLocalMusicGetters() {
        localMusicGetters.clear();
    }

    public void addSpotifyMusicGetter(SpotifyMusicGetter spotifyMusicGetter) {
        this.spotifyMusicGetter = spotifyMusicGetter;
    }

    public void addSoundCloudMusicGetter(SoundCloudMusicGetter soundCloudMusicGetter) {
        this.soundCloudMusicGetter = soundCloudMusicGetter;
    }

    public void addLocalMusicGetter(LocalMusicGetter localMusicGetter, String directory) {
        if (localMusicGetters == null)
        {
            localMusicGetters = new TreeMap<String, LocalMusicGetter>();
        }
        this.localMusicGetters.put(directory, localMusicGetter);
    }

    public SoundCloudMusicGetter getSoundCloudMusicGetter() {
        return soundCloudMusicGetter;
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

    public void initSoundCloudMusicGetter() {
        if (soundCloudMusicGetter != null) {
            soundCloudMusicGetter.init();
        }
    }

}
