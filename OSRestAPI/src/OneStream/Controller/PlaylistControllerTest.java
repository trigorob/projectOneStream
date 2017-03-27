package OneStream.Controller;

import OneStream.Controller.Util.Playlist;
import OneStream.Controller.Util.PlaylistSorter;
import OneStream.Controller.Util.Song;
import OneStream.PlaylistRepository;
import OneStream.SongRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;

/**
 * Created by ruspe_000 on 2017-03-21.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootApplication
@EntityScan(basePackages = {"OneStream"} )
@EnableMongoRepositories(basePackages = {"OneStream"})
@WebAppConfiguration
@Rollback
public class PlaylistControllerTest{
    Playlist p;
    Playlist p2;

    @Autowired
    PlaylistRepository playlistRepository;

    @Autowired
    SongRepository songRepository;

    private PlaylistController playlistController;
    private PlaylistRecommendationsController playlistRecommendationsController;
    private TopArtistsController topArtistsController;
    private TopAlbumController topAlbumController;
    private TopSongsController topSongsController;

    public void init() {
        initPlaylists();
        initPlaylistController();
    }

    public void initPlaylists() {
        Song s1 = new Song("name","uri",
                "artist", "album", "Local", 1, "albumart");
        Song s2 = new Song("name1","uri1",
                "artist1", "album", "Spotify", 2, "albumart2");
        Song s3 = new Song("name2","uri2",
                "artist1", "album2", "SoundCloud", 3, "albumart3");
        p = new Playlist("TestList", "Admin", new ArrayList<Song>());
        p2 = new Playlist("TestList2", "Admin2", new ArrayList<Song>());
        p.addSong(s1);
        p.addSong(s3);
        p2.addSong(s2);
    }

    public void initPlaylistController() {
        playlistController = new PlaylistController();
        playlistController.setPlaylistRepository(playlistRepository);
        playlistController.setSongRepository(songRepository);
        playlistController.create("", "", p);
        playlistController.create("", "", p2);
    }

    public void initRecommendedPlaylistController() {
        playlistRecommendationsController = new PlaylistRecommendationsController();
        playlistRecommendationsController.setPlaylistRepository(playlistRepository);
        playlistRecommendationsController.setSongRepository(songRepository);
    }

    public void initTopArtistController() {
        topArtistsController = new TopArtistsController();
        topArtistsController.setPlaylistRepository(playlistRepository);
        topArtistsController.setSongRepository(songRepository);
    }

    public void initTopAlbumController() {
        topAlbumController = new TopAlbumController();
        topAlbumController.setPlaylistRepository(playlistRepository);
        topAlbumController.setSongRepository(songRepository);
    }

    public void initTopSongController() {
        topSongsController = new TopSongsController();
        topSongsController.setPlaylistRepository(playlistRepository);
        topSongsController.setSongRepository(songRepository);
    }

    @Test
    public void testPlaylistController() {
        init();
        ArrayList<Playlist> playlists =
        playlistController.playlist("", "", 0, 1000,
                false, false, false);
        assert playlists.contains(p);
        assert playlists.contains(p2);

         playlists =
                playlistController.playlist("", "", 0, 1000,
                        true, false, false);
         assert !playlists.contains(p);
         assert playlists.contains(p2);

        playlists =
                playlistController.playlist("", "", 0, 1000,
                        false, true, false);

        assert playlists.contains(p);
        assert !playlists.contains(p2);

        playlists =
                playlistController.playlist("TestList", "Admin", 0, 1000,
                        false, true, false);
        assert playlists.contains(p);
        assert !playlists.contains(p2);

        playlists =
                playlistController.playlist("TestList2", "Admin", 0, 1000,
                        true, true, true);

        assert !playlists.contains(p);
        assert !playlists.contains(p2);

        playlists =
                playlistController.playlist("TestList2", "Admin2", 0, 1000,
                        false, false, false);

        assert !playlists.contains(p);
        assert playlists.contains(p2);

        p2.setName("TestList");
        playlistController.update("TestList2", "Admin2", p2);

        playlists = playlistController.playlist("TestList", "", 0, 1000,
                false, false, false);

        assert playlists.contains(p);
        assert playlists.contains(p2);

        playlists = playlistController.playlist("TestList2", "", 0, 1000,
                false, false, false);

        assert !playlists.contains(p);
        assert !playlists.contains(p2);

        playlistController.delete("TestList", "Admin2");

        playlists = playlistController.playlist("TestList", "", 0, 1000,
                false, false, false);


        assert playlists.contains(p);
        assert !playlists.contains(p2);
        
    }


    @Test
    public void testPlaylistSorter() {
        init();
        ArrayList<Playlist> playlists =
                playlistController.playlist("TestList", "", 0, 1000,
                        false, false, false);
        PlaylistSorter ps = new PlaylistSorter(playlists);
        playlists = ps.getSortedArray();
        assert playlists.contains(p);
        assert playlists.contains(p2);
        assert playlists.get(0).toString().equals(p.toString());
        assert playlists.get(1).toString().equals(p2.toString());
    }

        @Test
    public void testRecommendedPlaylists() {
        init();
        initRecommendedPlaylistController();

        ArrayList<Playlist> playlists =
                playlistRecommendationsController.playlist("", "",
                        "", 0, 1000,
                        false, false, false);
        assert !playlists.contains(p);
        assert !playlists.contains(p2);

        playlists =
                playlistRecommendationsController.playlist("name", "",
                        "", 0, 1000,
                        false, false, false);
        assert playlists.contains(p);
        assert playlists.contains(p2);

        playlists =
                playlistRecommendationsController.playlist("", "artist",
                        "", 0, 1000,
                        false, false, false);
        assert playlists.contains(p);
        assert playlists.contains(p2);

        playlists =
                playlistRecommendationsController.playlist("", "",
                        "album", 0, 1000,
                        false, false, false);
        assert playlists.contains(p);
        assert playlists.contains(p2);

        playlists =
                playlistRecommendationsController.playlist("name2", "artist1",
                        "", 0, 1000,
                        false, false, false);
        assert playlists.contains(p);
        assert playlists.contains(p2);

        playlists =
                playlistRecommendationsController.playlist("2", "",
                        "", 0, 1000,
                        false, false, false);
        assert playlists.contains(p);
        assert !playlists.contains(p2);

        playlists =
                playlistRecommendationsController.playlist("bad", "bad",
                        "bad", 0, 1000,
                        false, false, false);
        assert !playlists.contains(p);
        assert !playlists.contains(p2);
        resetRepo();
    }

    @Test
    public void testTopArtists() {
        init();
        initTopArtistController();

        ArrayList<Song> songs = p2.getSongInfo();
        songs.add(p.getSongInfo().get(1));

        Playlist expectedPlaylist
                = new Playlist("artist1", "", songs);

        ArrayList<Song> songs2 = p.getSongInfo();
        songs2.remove(p.getSongInfo().get(1));

        Playlist expectedPlaylist2
                = new Playlist("artist", "", songs2);


        ArrayList<Playlist> playlists =
                topArtistsController.playlist();

        assert !playlists.contains(p);
        assert !playlists.contains(p2);
        assert playlists.contains(expectedPlaylist);
        assert !playlists.contains(expectedPlaylist2);
        resetRepo();
    }

    @Test
    public void testTopAlbums() {
        init();
        initTopAlbumController();
        ArrayList<Song> songs = p2.getSongInfo();
        songs.add(p.getSongInfo().get(0));

        Playlist expectedPlaylist
                = new Playlist("album", "", songs);

        ArrayList<Song> songs2 = p.getSongInfo();
        songs2.remove(0);

        Playlist expectedPlaylist2
                = new Playlist("album2", "", songs2);


        ArrayList<Playlist> playlists =
                topAlbumController.playlist();

        assert !playlists.contains(p);
        assert !playlists.contains(p2);
        assert playlists.contains(expectedPlaylist);
        assert playlists.contains(expectedPlaylist2);
        resetRepo();
    }

    @Test
    public void testTopSongs() {
        init();
        initTopSongController();
        resetRepo();
        Playlist p = new Playlist();
        for (int i = 0; i < 55; i++)
        {
            Song s = new Song("testName" + i, "testUri" + i, "testArtist" + i,
                    "testAlbum" + i, "testType" + i, i, "" + i);
            p.addSong(s);
        }
        playlistController.create("TestList3", "TestOwner3", p);

        ArrayList<Playlist> playlists =
                topSongsController.song();

        assert !playlists.contains(p);
        assert !playlists.contains(p2);
        assert playlists.get(0).getName().equals("Top50");
        assert playlists.get(1).getName().contains("50 to 55");
        assert playlists.get(0).getSongInfo().size() == 50;
        assert playlists.get(1).getSongInfo().size() == 5;
        assert playlists.get(0).getSongInfo().get(0).getPosition() == 54;
        assert playlists.get(0).getSongInfo().get(1).getPosition() == 53;
        assert playlists.get(0).getSongInfo().get(49).getPosition() == 5;
        assert playlists.get(1).getSongInfo().get(0).getPosition() == 4;
        assert playlists.get(1).getSongInfo().get(4).getPosition() == 0;
        resetRepo();
    }

    public void resetRepo() {
        playlistRepository.deleteAll();
        songRepository.deleteAll();
    }
}