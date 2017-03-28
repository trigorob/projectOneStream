package music.onestream.musicgetter;

import android.os.AsyncTask;

import java.util.ArrayList;

import music.onestream.playlist.Playlist;
import music.onestream.song.Song;
import music.onestream.util.AsyncResponse;
import music.onestream.util.Constants;

/**
 * Created by ruspe_000 on 2017-03-09.
 */

public class AlbumArtistGenreLoader extends AsyncTask {
    public AsyncResponse SAR;
    private ArrayList<Playlist> artists;
    private ArrayList<Playlist> albums;
    private ArrayList<Playlist> genres;

    @Override
    protected Object doInBackground(Object[] params) {
        this.artists = (ArrayList<Playlist>) params[0];
        this.albums = (ArrayList<Playlist>) params[1];
        this.genres = (ArrayList<Playlist>) params[2];
        ArrayList<Song> songs = (ArrayList<Song>) params[3];
        artists = addSongsToList(songs, Constants.artists, artists);
        albums = addSongsToList(songs, Constants.albums, albums);
        genres = addSongsToList(songs, Constants.genres, genres);
        ArrayList<ArrayList<Playlist>> retVal = new ArrayList<ArrayList<Playlist>>();
        retVal.add(artists);
        retVal.add(albums);
        retVal.add(genres);
        return retVal;
    }

    @Override
    protected void onPostExecute(Object result) {
        Object[] retObject = new Object[2];
        retObject[0] = Constants.artistsAlbumsGenresLoader;
        SAR.processFinish(retObject);
    }

    public ArrayList<Playlist> addSongsToList(ArrayList<Song> songs, String type,
                                                ArrayList<Playlist> list) {
        for (Song song : songs) {
            Playlist p;
            if (type.equals(Constants.artists)) {
                p = new Playlist(song.getArtist(), "", new ArrayList<Song>());
            }
            else if (type.equals(Constants.albums))
            {
                p = new Playlist(song.getAlbum(), "", new ArrayList<Song>());
            }
            else
            {
                p = new Playlist(song.getGenre(), "", new ArrayList<Song>());
            }
            if (!p.getName().equals(Constants.defaultArtistsAlbumGenreName)) {
                if (!list.contains(p)) {
                    p.addSong(song);
                    list.add(p);
                } else {
                    int playlistLocation = list.indexOf(p);
                    list.get(playlistLocation).addSong(song);
                }
            }
        }
        return list;
    }
}
