package OneStream.Controller;

import OneStream.PlaylistRepository;
import OneStream.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by ruspe_000 on 2017-03-21.
 */
public abstract class OneStreamController {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    public void setSongRepository(SongRepository sr) {
        this.songRepository = sr;
    }

    public void setPlaylistRepository(PlaylistRepository pr) {
        this.playlistRepository = pr;
    }

    public SongRepository getSongRepository() {
        return songRepository;
    }

    public PlaylistRepository getPlaylistRepository() {
        return playlistRepository;
    }
}
