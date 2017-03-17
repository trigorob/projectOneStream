package music.onestream.musicgetter;

import music.onestream.playlist.PlaylistHandler;

/**
 * Created by ruspe_000 on 2017-02-03.
 */

public class GoogleMusicGetter implements MusicGetter {
    int gmusicSongOffset;
    String token;
    PlaylistHandler handler;
    private final int spotifySongCap = 1000;

    public GoogleMusicGetter(String token, PlaylistHandler handler) {
        this.gmusicSongOffset = 0;
        this.token  = token;
        this.handler = handler;
    }
    @Override
    public void init() {

        while (gmusicSongOffset < spotifySongCap) {
            SpotifyMusicLoader spotifyMusicLoader = new SpotifyMusicLoader();
            spotifyMusicLoader.SAR = handler;
            Object[] params = new Object[2];
            params[0] = token;
            params[1] = gmusicSongOffset;
            spotifyMusicLoader.execute(params);
            gmusicSongOffset += 50;
        }
    }

    //GoogleMusicAPI api = new GoogleMusicAPI("clientId");
}
