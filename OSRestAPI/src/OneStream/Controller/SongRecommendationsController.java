package OneStream.Controller;

import OneStream.Controller.Util.Itemgetter;
import OneStream.Controller.Util.Song;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RequestMapping("OneStream/Songs/Recommendations")
@RestController
public class SongRecommendationsController extends OneStreamController {

    @GetMapping
    public ArrayList<Song> song(@RequestParam(value = "name", defaultValue = "") String name,
                                @RequestParam(value = "artist", defaultValue = "") String artist,
                                @RequestParam(value = "album", defaultValue = "") String album,
                                @RequestParam(value = "excludeLocal", defaultValue = "false") boolean excludeLocal,
                                @RequestParam(value = "excludeSpotify", defaultValue = "false") boolean excludeSpotify,
                                @RequestParam(value = "excludeSoundCloud", defaultValue = "false") boolean excludeSoundCloud) {

        Itemgetter ig = new Itemgetter();
        ArrayList<Song> allSongs = (ArrayList<Song>) getSongRepository().findAll();
                //All playlists filtered by search params
        allSongs = ig.getSongs("", "", "", excludeLocal,excludeSpotify,excludeSoundCloud, allSongs);
        ArrayList<Song> filteredSongs = new ArrayList<Song>();
        if (allSongs == null)
        {
            return filteredSongs;
        }
        Song sTemp = new Song();
        sTemp.setName(name);
        sTemp.setArtist(artist);
        sTemp.setAlbum(album);
            for (int i = 0; i < allSongs.size(); i++) {
                Song s = allSongs.get(i);

                if (Song.compareAllSong(sTemp, s)) {
                    filteredSongs.add(s);
                }
            }
        return filteredSongs;
    }
}