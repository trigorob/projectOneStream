package music.onestream.musicgetter;

import music.onestream.playlist.PlaylistHandler;
import music.onestream.util.Constants;

/**
 * Created by ruspe_000 on 2017-02-03.
 */

public class SoundCloudMusicGetter implements MusicGetter {
    int soundCloudSongOffset;
    String token;
    PlaylistHandler handler;

    public SoundCloudMusicGetter(String token, PlaylistHandler handler) {
        this.soundCloudSongOffset = 0;
        this.token  = token;
        this.handler = handler;
    }
    @Override
    public void init() {

        while (soundCloudSongOffset < Constants.spotifySongCap) {
            SoundCloudMusicLoader soundCloudMusicLoader = new SoundCloudMusicLoader();
            soundCloudMusicLoader.SAR = handler;
            Object[] params = new Object[2];
            params[0] = token;
            params[1] = soundCloudSongOffset;
            soundCloudMusicLoader.execute(params);
            soundCloudSongOffset += Constants.soundCloudLoadStepSize;
        }
    }

}
