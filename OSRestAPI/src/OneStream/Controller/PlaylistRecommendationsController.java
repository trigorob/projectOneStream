package OneStream.Controller;

import OneStream.Controller.Util.Itemgetter;
import OneStream.Controller.Util.Playlist;
import OneStream.Controller.Util.Song;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


@RequestMapping("OneStream/Playlists/Recommendations")
@RestController
public class PlaylistRecommendationsController extends OneStreamController {

    @GetMapping
    public ArrayList<Playlist> playlist(@RequestParam(value = "name", defaultValue = "") String name,
                                        @RequestParam(value = "artist", defaultValue = "") String artist,
                                        @RequestParam(value = "album", defaultValue = "") String album,
                                        @RequestParam(value = "minLength", defaultValue = "0") int minLength,
                                        @RequestParam(value = "maxLength", defaultValue = "1000") int maxLength,
                                        @RequestParam(value = "excludeLocal", defaultValue = "false") boolean excludeLocal,
                                        @RequestParam(value = "excludeSpotify", defaultValue = "false") boolean excludeSpotify,
                                        @RequestParam(value = "excludeSoundCloud", defaultValue = "false") boolean excludeSoundCloud) {
        Itemgetter ig = new Itemgetter();
        ArrayList<Playlist> allPlaylists = (ArrayList<Playlist>) getPlaylistRepository().findAll();//All playlists filtered by search params
        allPlaylists = ig.getPlaylists("","",minLength,maxLength,excludeLocal,excludeSpotify,excludeSoundCloud,
                allPlaylists);
        ArrayList<Playlist> filteredPlaylists = new ArrayList<Playlist>();

        ArrayList<Song> songs;

        if (allPlaylists == null)
        {
            return filteredPlaylists;
        }
        Song sTemp = new Song();
        sTemp.setName(name);
        sTemp.setArtist(artist);
        sTemp.setAlbum(album);
        for (Playlist playlist : allPlaylists)
        {
            songs = playlist.getSongInfo();
            if (songs != null) {
                for (int i = 0; i < songs.size(); i++) {
                    Song s = songs.get(i);
                    if (Song.compareAllSong(sTemp, s)) {
                        filteredPlaylists.add(playlist);
                        i += songs.size();
                    }
                }
            }
        }
        return filteredPlaylists;
    }
}