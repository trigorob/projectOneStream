package music.onestream;

import org.json.JSONArray;
import org.junit.Test;

import java.util.ArrayList;

import music.onestream.playlist.Playlist;
import music.onestream.song.Song;
import music.onestream.util.JSONExtractor;
import music.onestream.util.MusicSorter;
import music.onestream.util.PlaylistSorter;

/**
 * Created by ruspe_000 on 2017-03-12.
 */

public class MusicSorterTest {

    private final String ALPH_ASC = "ALPH-ASC";
    private final String ALPH_DESC = "ALPH-DESC";
    private final String ALPH_ASC_ARTIST = "ALPH-ASC-ARTIST";
    private final String ALPH_DESC_ARTIST = "ALPH-DESC-ARTIST";
    private final String ALPH_ASC_ALBUM = "ALPH-ASC-ALBUM";
    private final String ALPH_DESC_ALBUM = "ALPH-DESC-ALBUM";

    private Playlist playlist;
    private Song song1;
    private Song song2;
    private Song song3;

    private ArrayList<Song> sortedSongs;

    @Test
    public void musicSorterTest() throws Exception {
        initPlaylists();
        ArrayList<Song> initialSongInfo = playlist.getSongInfo();
        assert initialSongInfo.get(0).equals(song1);
        assert initialSongInfo.get(1).equals(song2);
        assert initialSongInfo.get(2).equals(song3);
        MusicSorter musicSorter = new MusicSorter(playlist.getSongInfo(), ALPH_ASC);
        sortedSongs = (ArrayList<Song>) musicSorter.getRetArr()[0];
        assert sortedSongs.size() == 3;
        assert sortedSongs.get(0).equals(initialSongInfo.get(0));
        assert sortedSongs.get(1).equals(initialSongInfo.get(1));
        assert sortedSongs.get(2).equals(initialSongInfo.get(2));
        musicSorter = new MusicSorter(playlist.getSongInfo(), ALPH_DESC);
        sortedSongs = (ArrayList<Song>) musicSorter.getRetArr()[0];
        assert sortedSongs.size() == 3;
        assert sortedSongs.get(0).equals(initialSongInfo.get(2));
        assert sortedSongs.get(1).equals(initialSongInfo.get(1));
        assert sortedSongs.get(2).equals(initialSongInfo.get(0));
        musicSorter = new MusicSorter(playlist.getSongInfo(), ALPH_ASC_ARTIST);
        sortedSongs = (ArrayList<Song>) musicSorter.getRetArr()[0];
        assert sortedSongs.size() == 3;
        assert sortedSongs.get(0).equals(initialSongInfo.get(2));
        assert sortedSongs.get(1).equals(initialSongInfo.get(0));
        assert sortedSongs.get(2).equals(initialSongInfo.get(1));
        musicSorter = new MusicSorter(playlist.getSongInfo(), ALPH_DESC_ARTIST);
        sortedSongs = (ArrayList<Song>) musicSorter.getRetArr()[0];
        assert sortedSongs.size() == 3;
        assert sortedSongs.get(0).equals(initialSongInfo.get(1));
        assert sortedSongs.get(1).equals(initialSongInfo.get(0));
        assert sortedSongs.get(2).equals(initialSongInfo.get(2));
        musicSorter = new MusicSorter(playlist.getSongInfo(), ALPH_ASC_ALBUM);
        sortedSongs = (ArrayList<Song>) musicSorter.getRetArr()[0];
        assert sortedSongs.size() == 3;
        assert sortedSongs.get(0).equals(initialSongInfo.get(2));
        assert sortedSongs.get(1).equals(initialSongInfo.get(1));
        assert sortedSongs.get(2).equals(initialSongInfo.get(0));
        musicSorter = new MusicSorter(playlist.getSongInfo(), ALPH_DESC_ALBUM);
        sortedSongs = (ArrayList<Song>) musicSorter.getRetArr()[0];
        assert sortedSongs.size() == 3;
        assert sortedSongs.get(0).equals(initialSongInfo.get(0));
        assert sortedSongs.get(1).equals(initialSongInfo.get(1));
        assert sortedSongs.get(2).equals(initialSongInfo.get(2));



    }

    public void initPlaylists() {
        playlist = new Playlist();
        playlist.setName("TestList");
        playlist.setOwner("TestOwner");
        song1 = new Song("ZZZZ", "ZZZZ", "BBBB", "CCCC", "ZZZZ", 0, "ZZZZ");
        playlist.addSong(song1);
        song2 = new Song("Self Control", "spotify:track:2Yxa3k0CecfZ5HVWQyxNvy", "Scissor Sisters",
                "Magic Hour", "Spotify", 4, "https://i.scdn.co/image/bfb21f1026245457738ed73f535d079bf6450ada");
        playlist.addSong(song2);
        song3 = new Song("AAAA", "AAAA", "AAAA", "DDDD", "AAA", 0, "AAAA");
        playlist.addSong(song3);
    }
}
