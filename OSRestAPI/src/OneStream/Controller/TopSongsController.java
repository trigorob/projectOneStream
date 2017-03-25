package OneStream.Controller;

import OneStream.Controller.Util.Playlist;
import OneStream.Controller.Util.Song;
import OneStream.Controller.Util.SongSorter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


@RequestMapping("OneStream/Songs/Top")
@RestController
public class TopSongsController extends OneStreamController {

    @GetMapping
    public ArrayList<Playlist> song()
    {
        ArrayList<Song> allSongs = (ArrayList<Song>) getSongRepository().findAll();
        ArrayList<Song> badSongs = new ArrayList<Song>();
        for (Song song: allSongs)
        {
            if (song.getType().equals("Local")) {
                badSongs.add(song);
            }
        }
        allSongs.removeAll(badSongs);
        SongSorter sorter = new SongSorter(allSongs, "SONG");
        allSongs = (ArrayList<Song>) sorter.getRetArr()[0];
        ArrayList<Playlist> songsLists = new ArrayList<Playlist>();
        int offset = 0;
        String name = "";
        while (true) {
            if (offset == 0)
            {
                name = "Top50";
            }
            else
            {
                name = offset + " to " + Math.min((offset+50), allSongs.size());
            }
            Playlist p = new Playlist(name, "Admin", new ArrayList<Song>());
            for (int i = offset; i < offset+50; i++) {
                if (i >= allSongs.size()) {
                    if (!songsLists.contains(p)) {
                        songsLists.add(p);
                    }
                    return songsLists;
                }
                p.addSong(allSongs.get(i));
                if (i % 50 == 0) {
                    songsLists.add(p);
                }
            }
            offset+= 50;
        }
    }
}