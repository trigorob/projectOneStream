package music.onestream;

import org.junit.Test;

import java.util.ArrayList;

import music.onestream.playlist.Playlist;
import music.onestream.util.PlaylistSorter;

/**
 * Created by ruspe_000 on 2017-03-12.
 */

public class PlaylistSorterTest {

    private final String ALPH_ASC = "ALPH-ASC";
    private final String ALPH_DESC = "ALPH-DESC";

    private Playlist playlist;
    private Playlist playlist2;
    private Playlist playlist3;
    private ArrayList<Playlist> playlists;

    private ArrayList<Playlist> sortedPlaylists;

    @Test
    public void playlistSorterTest() throws Exception {
        initPlaylists();
        assert playlists.get(0).equals(playlist);
        assert playlists.get(1).equals(playlist2);
        assert playlists.get(2).equals(playlist3);
        PlaylistSorter ps = new PlaylistSorter(playlists, ALPH_ASC);
        sortedPlaylists =  ps.getSortedArray();
        assert sortedPlaylists.size() == 3;
        assert sortedPlaylists.get(0).equals(playlist3);
        assert sortedPlaylists.get(1).equals(playlist2);
        assert sortedPlaylists.get(2).equals(playlist);
        ps = new PlaylistSorter(playlists, ALPH_DESC);
        sortedPlaylists =  ps.getSortedArray();
        assert sortedPlaylists.size() == 3;
        assert sortedPlaylists.get(0).equals(playlist);
        assert sortedPlaylists.get(1).equals(playlist2);
        assert sortedPlaylists.get(2).equals(playlist3);
    }

    public void initPlaylists() {
        playlist = new Playlist();
        playlist.setName("TestList");
        playlist.setOwner("TestOwner");
        playlist2 = new Playlist("OtherList", "OtherOwner", null);
        playlist3 = new Playlist("Alist", "ATeam", null);
        playlists = new ArrayList<Playlist>();
        playlists.add(playlist);
        playlists.add(playlist2);
        playlists.add(playlist3);
    }
}
