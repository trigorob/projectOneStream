package music.onestream.musicgetter;

import music.onestream.playlist.PlaylistHandler;
import music.onestream.util.Constants;

/**
 * Created by ruspe_000 on 2017-02-03.
 */

public class SoundCloudMusicGetter implements MusicGetter {
    int soundCloudSongOffset;
    String token;
    String href;
    PlaylistHandler handler;

    public SoundCloudMusicGetter(String token, String nextHref, PlaylistHandler handler) {
        this.soundCloudSongOffset = 0;
        this.token  = token;
        this.handler = handler;
        this.href = nextHref;
    }
    @Override
    public void init() {
            SoundCloudMusicLoader soundCloudMusicLoader = new SoundCloudMusicLoader();
            soundCloudMusicLoader.SAR = handler;
            Object[] params = new Object[2];
            params[0] = token;
            params[1] = href;
            soundCloudMusicLoader.execute(params);
            soundCloudSongOffset += Constants.soundCloudLoadStepSize;
    }

}
