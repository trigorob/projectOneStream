package music.onestream.musicgetter;

import music.onestream.playlist.PlaylistHandler;
import music.onestream.util.Constants;

/**
 * Created by ruspe_000 on 2017-02-26.
 */

public class SpotifyMusicGetter implements MusicGetter {

    private int spotifySongOffset;
    private String token;
    private PlaylistHandler handler;

    public SpotifyMusicGetter(String token, PlaylistHandler handler) {
        this.spotifySongOffset = 0;
        this.token  = token;
        this.handler = handler;
    }
    @Override
    public void init() {

        while (spotifySongOffset < Constants.spotifySongCap) {
            SpotifyMusicLoader spotifyMusicLoader = new SpotifyMusicLoader();
            spotifyMusicLoader.SAR = handler;
            Object[] params = new Object[2];
            params[0] = token;
            params[1] = spotifySongOffset;
            spotifyMusicLoader.execute(params);
            spotifySongOffset += Constants.spotifyLoadStepSize;
        }
    }
}
