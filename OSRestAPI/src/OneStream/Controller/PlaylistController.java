package OneStream.Controller;

import OneStream.Controller.Util.Itemgetter;
import OneStream.Controller.Util.Playlist;
import OneStream.Controller.Util.Song;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RequestMapping("OneStream/Playlists")
@RestController
@Configurable
public class PlaylistController extends OneStreamController {

    @GetMapping
    public ArrayList<Playlist> playlist(@RequestParam(value="name", defaultValue="") String name,
                                        @RequestParam(value="owner", defaultValue="")String owner,
                                        @RequestParam(value="minLength", defaultValue="0") int minLength,
                                        @RequestParam(value="maxLength", defaultValue="1000") int maxLength,
                                        @RequestParam(value="excludeLocal", defaultValue="false") boolean excludeLocal,
                                        @RequestParam(value="excludeSpotify", defaultValue="false") boolean excludeSpotify,
                                        @RequestParam(value="excludeSoundCloud", defaultValue="false") boolean excludeSoundCloud)
    {
        Itemgetter ig = new Itemgetter();
        ArrayList<Playlist> allPlaylist = (ArrayList<Playlist>) getPlaylistRepository().findAll();
        return ig.getPlaylists(name, owner, minLength, maxLength, excludeLocal, excludeSpotify, excludeSoundCloud, allPlaylist);
    }


    @DeleteMapping
    public void delete(@RequestParam(value="name", defaultValue="") String name,
                       @RequestParam(value="owner", defaultValue="")String owner) {

        ArrayList<Playlist> p = new ArrayList<Playlist>();
        if (!name.equals("")) {
             p = getPlaylistRepository().findByNameAndOwner(name, owner);
        }
        else
        {
            p = getPlaylistRepository().findByOwner(owner);
        }
        if (p != null) {
            for (int i = 0; i < p.size(); i++)
            {
                this.getPlaylistRepository().delete(p.get(i).get_id());
            }
        }
        return;
    }


    @PutMapping
    public Playlist update(@RequestParam(value="name", defaultValue="") String name,
                           @RequestParam(value="owner", defaultValue="")String owner,
                           @RequestBody Playlist playlist) {

        if (name.equals(""))
        {
            name = playlist.getName();
        }
        if (owner.equals(""))
        {
            owner = playlist.getOwner();
        }
        ArrayList<Song> songs = playlist.getSongInfo();

        updateSongs(songs);

        ArrayList<Playlist> listIfExists = getPlaylistRepository().findByNameAndOwner(name, owner);
        if (listIfExists != null && listIfExists.size() > 0)
        {
            this.getPlaylistRepository().delete(listIfExists.get(0).get_id());
            return this.getPlaylistRepository().save(playlist);
        }
        //Playlist doesn't exist
        return null;
    }

    @PostMapping
    public Playlist create(@RequestParam(value="name", defaultValue="") String name,
                           @RequestParam(value="owner", defaultValue="")String owner,
                           @RequestBody Playlist playlist)
    {
        if (name.equals(""))
        {
            name = playlist.getName();
        }
        if (owner.equals(""))
        {
            owner = playlist.getOwner();
        }
        ArrayList<Song> songs = playlist.getSongInfo();

        updateSongs(songs);

        ArrayList<Playlist> listIfExists = getPlaylistRepository().findByNameAndOwner(name, owner);
        if (listIfExists == null || listIfExists.size() == 0)
        {
            return this.getPlaylistRepository().save(playlist);
        }
        //Playlist already exists
        return null;
    }

    private void updateSongs(ArrayList<Song> songs) {
        for (Song s : songs)
        {
                ArrayList<Song> songIfExists = getSongRepository().findByUri(s.getUri());
                if (songIfExists != null && songIfExists.size() > 0)
                {
                    getSongRepository().delete(songIfExists);
                    s.setPosition(songIfExists.get(0).getPosition() + 1);
                }
                this.getSongRepository().save(s);
        }
    }

}
