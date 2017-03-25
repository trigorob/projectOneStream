package OneStream.Controller;

import OneStream.Controller.Util.Playlist;
import OneStream.Controller.Util.PlaylistSorter;
import OneStream.Controller.Util.Song;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


@RequestMapping("OneStream/Albums/Top")
@RestController
public class TopAlbumController extends OneStreamController {

    @GetMapping
    public ArrayList<Playlist> playlist()
    {
        ArrayList<Song> allSongs = (ArrayList<Song>) getSongRepository().findAll();
        ArrayList<Playlist> topAlbums = new ArrayList<Playlist>();
        ArrayList<Playlist> allAlbumPlaylists = addSongsToAlbums(allSongs);
        for (int i = 0; i < 50; i++) {
            if (i >= allAlbumPlaylists.size())
            {
                PlaylistSorter ps = new PlaylistSorter(topAlbums, "Playlist");
                return ps.getSortedArray();
            }
            topAlbums.add(allAlbumPlaylists.get(i));
        }
        PlaylistSorter ps = new PlaylistSorter(topAlbums, "Playlist");
        return ps.getSortedArray();
    }

    public ArrayList<Playlist> addSongsToAlbums(ArrayList<Song> songs) {
        ArrayList<Playlist> albums = new ArrayList<Playlist>();
        for (Song song : songs) {
            if (!song.getType().equals("Local")) {
                Playlist p = new Playlist(song.getAlbum(), "", new ArrayList<Song>());
                if (!albums.contains(p)) {
                    p.addSong(song);
                    albums.add(p);
                } else {
                    albums.get(albums.indexOf(p)).addSong(song);
                }
            }
        }
        return albums;
    }
}