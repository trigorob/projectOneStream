package OneStream.Controller;

import OneStream.Controller.Util.Itemgetter;
import OneStream.Controller.Util.Song;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


@RequestMapping("OneStream/Songs")
@RestController
public class SongController extends OneStreamController {

    @GetMapping
    public ArrayList<Song> song(@RequestParam(value="name", defaultValue="") String name,
                                @RequestParam(value="artist", defaultValue="") String artist,
                                @RequestParam(value="album", defaultValue="") String album,
                                @RequestParam(value="album", defaultValue="") String genre,
                                @RequestParam(value="excludeLocal", defaultValue="false") boolean excludeLocal,
                                @RequestParam(value="excludeSpotify", defaultValue="false") boolean excludeSpotify,
                                @RequestParam(value="excludeSoundCloud", defaultValue="false") boolean excludeSoundCloud)
    {
        Itemgetter ig = new Itemgetter();
        ArrayList<Song> allSongs = (ArrayList<Song>) getSongRepository().findAll();
        return ig.getSongs(name, artist, album, genre, excludeLocal, excludeSpotify, excludeSoundCloud, allSongs);
    }
}
