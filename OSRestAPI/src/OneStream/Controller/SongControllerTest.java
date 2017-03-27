package OneStream.Controller;

import OneStream.Controller.Util.Playlist;
import OneStream.Controller.Util.Song;
import OneStream.Controller.Util.SongSorter;
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
public class SongControllerTest {

    @Autowired
    PlaylistRepository playlistRepository;

    @Autowired
    SongRepository songRepository;


    private SongController songController;
    private SongRecommendationsController songRecommendationsController;
    private ArrayList<Song> allSongs;
    private PlaylistController playlistController;
    Playlist p;

    public void init() {
        initRepo();
        initPlaylistController();
        initSongController();
    }

    public void initRepo() {
        Song s1 = new Song("name", "uri",
                "artist", "album", "Local", 1, "albumart");
        Song s2 = new Song("name1", "uri1",
                "artist1", "album", "Spotify", 2, "albumart2");
        Song s3 = new Song("name2", "uri2",
                "artist1", "album2", "SoundCloud", 3, "albumart3");
        allSongs = new ArrayList<Song>();
        allSongs.add(s1);
        allSongs.add(s2);
        allSongs.add(s3);
        p = new Playlist();
        p.setSongInfo(allSongs);
    }

    public void initPlaylistController() {
        playlistController = new PlaylistController();
        playlistController.setPlaylistRepository(playlistRepository);
        playlistController.setSongRepository(songRepository);
        playlistController.create("", "", p);
    }

    public void initSongController() {
        songController = new SongController();
        songController.setSongRepository(songRepository);
    }

    public void initSongRecommendationsController() {
        songRecommendationsController = new SongRecommendationsController();
        songRecommendationsController.setSongRepository(songRepository);
    }


    @Test
    public void testGetSongs() {
        init();
        ArrayList<Song> songs =songController.song("","","",
                false,false,false);

        assert songs.contains(allSongs.get(0));
        assert songs.contains(allSongs.get(1));
        assert songs.contains(allSongs.get(2));

        songs =songController.song("name2","DUMMY-VAR","album2",
                false,false,false);

        assert !songs.contains(allSongs.get(0));
        assert !songs.contains(allSongs.get(1));
        assert songs.contains(allSongs.get(2));

        songs =songController.song("DUMMY-VAR","artist1","DUMMY-VAR",
                false,false,false);

        assert !songs.contains(allSongs.get(0));
        assert songs.contains(allSongs.get(1));
        assert songs.contains(allSongs.get(2));

        songs =songController.song("name","artist","album",
                false,false,false);

        assert songs.contains(allSongs.get(0));
        assert songs.contains(allSongs.get(1));
        assert songs.contains(allSongs.get(2));

        songs =songController.song("name","artist","album",
                true,false,false);

        assert !songs.contains(allSongs.get(0));
        assert songs.contains(allSongs.get(1));
        assert songs.contains(allSongs.get(2));

        songs =songController.song("name","artist","album",
                false,true,false);

        assert songs.contains(allSongs.get(0));
        assert !songs.contains(allSongs.get(1));
        assert songs.contains(allSongs.get(2));

        songs =songController.song("name","artist","album",
                false,false,true);

        assert songs.contains(allSongs.get(0));
        assert songs.contains(allSongs.get(1));
        assert !songs.contains(allSongs.get(2));
    }

    @Test
    public void testSongSorter() {
        init();
        ArrayList<Song> songs =songController.song("","","",
                false,false,false);

        SongSorter songSorter = new SongSorter(songs);
        songs = songSorter.getSortedArray();
        assert songs.contains(allSongs.get(2));
        assert songs.contains(allSongs.get(1));
        assert songs.contains(allSongs.get(0));
        assert songs.get(2).toString().equals(allSongs.get(0).toString());
        assert songs.get(1).toString().contains(allSongs.get(1).toString());
        assert songs.get(0).toString().contains(allSongs.get(2).toString());

    }

    @Test
    public void testGetRecommendations() {
        init();
        initSongRecommendationsController();
        ArrayList<Song> songs =songRecommendationsController.song("","","",
                false,false,false);

        assert !songs.contains(allSongs.get(0));
        assert !songs.contains(allSongs.get(1));
        assert !songs.contains(allSongs.get(2));

        songs =songRecommendationsController.song("name2","DUMMY-VAR","album2",
                false,false,false);

        assert songs.contains(allSongs.get(0));
        assert songs.contains(allSongs.get(1));
        assert songs.contains(allSongs.get(2));

        songs =songRecommendationsController.song("DUMMY-VAR","artist1","DUMMY-VAR",
                false,false,false);

        assert songs.contains(allSongs.get(0));
        assert songs.contains(allSongs.get(1));
        assert songs.contains(allSongs.get(2));

        songs =songRecommendationsController.song("name","artist","album",
                false,false,false);

        assert songs.contains(allSongs.get(0));
        assert songs.contains(allSongs.get(1));
        assert songs.contains(allSongs.get(2));

        songs =songRecommendationsController.song("name","artist","album",
                true,false,false);

        assert !songs.contains(allSongs.get(0));
        assert songs.contains(allSongs.get(1));
        assert songs.contains(allSongs.get(2));

        songs =songRecommendationsController.song("name","artist","album",
                false,true,false);

        assert songs.contains(allSongs.get(0));
        assert !songs.contains(allSongs.get(1));
        assert songs.contains(allSongs.get(2));

        songs =songRecommendationsController.song("name","artist","album",
                false,false,true);

        assert songs.contains(allSongs.get(0));
        assert songs.contains(allSongs.get(1));
        assert !songs.contains(allSongs.get(2));
    }


}