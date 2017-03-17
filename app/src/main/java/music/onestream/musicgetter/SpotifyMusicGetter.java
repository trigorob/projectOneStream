package music.onestream.musicgetter;

import music.onestream.playlist.PlaylistHandler;

/**
 * Created by ruspe_000 on 2017-02-26.
 */

public class SpotifyMusicGetter implements MusicGetter {

    private int spotifySongOffset;
    private String token;
    private PlaylistHandler handler;
    private final int spotifySongCap = 1000;

    public SpotifyMusicGetter(String token, PlaylistHandler handler) {
        this.spotifySongOffset = 0;
        this.token  = token;
        this.handler = handler;
    }
    @Override
    public void init() {

        while (spotifySongOffset < spotifySongCap) {
            SpotifyMusicLoader spotifyMusicLoader = new SpotifyMusicLoader();
            spotifyMusicLoader.SAR = handler;
            Object[] params = new Object[2];
            params[0] = token;
            params[1] = spotifySongOffset;
            spotifyMusicLoader.execute(params);
            spotifySongOffset += 50;
        }
    }
}
