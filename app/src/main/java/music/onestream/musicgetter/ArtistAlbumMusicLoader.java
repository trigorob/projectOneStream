package music.onestream.musicgetter;

import android.os.AsyncTask;

import java.util.ArrayList;

import music.onestream.playlist.Playlist;
import music.onestream.playlist.PlaylistHandler;
import music.onestream.song.Song;
import music.onestream.util.AsyncResponse;

/**
 * Created by ruspe_000 on 2017-03-09.
 */

public class ArtistAlbumMusicLoader extends AsyncTask {
    public AsyncResponse SAR;
    private ArrayList<Playlist> artists;
    private ArrayList<Playlist> albums;

    @Override
    protected Object doInBackground(Object[] params) {
        this.artists = (ArrayList<Playlist>) params[0];
        this.albums = (ArrayList<Playlist>) params[1];
        ArrayList<Song> songs = (ArrayList<Song>) params[2];
        artists = addSongToArtists(songs);
        albums = addSongToAlbums(songs);
        ArrayList<ArrayList<Playlist>> retVal = new ArrayList<ArrayList<Playlist>>();
        retVal.add(artists);
        retVal.add(albums);
        return retVal;
    }

    @Override
    protected void onPostExecute(Object result) {
        Object[] retObject = new Object[2];
        retObject[0] = "ArtistAlbumMusicLoader";
        SAR.processFinish(retObject);
    }

    public ArrayList<Playlist> addSongToArtists(ArrayList<Song> songs) {
        for (Song song : songs) {
            Playlist p = new Playlist(song.getArtist(), "", new ArrayList<Song>());
            if (!artists.contains(p)) {
                p.addSong(song);
                artists.add(p);
            } else {
                int artistPlaylist = artists.indexOf(p);
                artists.get(artistPlaylist).addSong(song);
            }
        }
        return artists;
    }
    public ArrayList<Playlist> addSongToAlbums(ArrayList<Song> songs) {
        for (Song song: songs) {
            Playlist p = new Playlist(song.getAlbum(), "", new ArrayList<Song>());
            if (!albums.contains(p)) {
                p.addSong(song);
                albums.add(p);
            } else {
                int artistPlaylist = albums.indexOf(p);
                albums.get(artistPlaylist).addSong(song);
            }
        }
        return albums;
    }

}
