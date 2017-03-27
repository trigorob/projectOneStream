package OneStream.Controller;

import OneStream.Controller.Util.Playlist;
import OneStream.Controller.Util.PlaylistSorter;
import OneStream.Controller.Util.Song;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


@RequestMapping("OneStream/Artists/Top")
@RestController
public class TopArtistsController extends OneStreamController {

    @GetMapping
    public ArrayList<Playlist> playlist()
    {
        ArrayList<Song> allSongs = (ArrayList<Song>) getSongRepository().findAll();
        ArrayList<Playlist> topArtists = new ArrayList<Playlist>();
        ArrayList<Playlist> allArtistPlaylists = addSongsToArtists(allSongs);
        for (int i = 0; i < 50; i++) {
            if (i >= allArtistPlaylists.size())
            {
                PlaylistSorter ps = new PlaylistSorter(topArtists);
                topArtists = ps.getSortedArray();
                return topArtists;
            }
            topArtists.add(allArtistPlaylists.get(i));
        }
        PlaylistSorter ps = new PlaylistSorter(topArtists);
        topArtists = ps.getSortedArray();
        return topArtists;
    }

    public ArrayList<Playlist> addSongsToArtists(ArrayList<Song> songs) {
        ArrayList<Playlist> artists = new ArrayList<Playlist>();
        for (Song song : songs) {
            if (!song.getType().equals("Local")) {
                Playlist p = new Playlist(song.getArtist(), "", new ArrayList<Song>());
                if (!artists.contains(p)) {
                    p.addSong(song);
                    artists.add(p);
                } else {
                    int artistPlaylist = artists.indexOf(p);
                    artists.get(artistPlaylist).addSong(song);
                }
            }
        }
        return artists;
    }
}