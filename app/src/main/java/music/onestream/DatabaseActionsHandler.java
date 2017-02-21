package music.onestream;

import android.os.AsyncTask;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import music.onestream.database.OneStreamRestService;

/**
 * Created by ruspe_000 on 2017-02-20.
 */

public class DatabaseActionsHandler extends AsyncTask {
    AsyncResponse SAR;

    protected void onPostExecute(Object result) {
        SAR.processFinish(result);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        String action = (String) params[0];

        if (action.equals("CreatePlaylist"))
        {
            Playlist playlist = (Playlist) params[1];
            createNewPlaylist(playlist);
        }
        else if (action.equals("GetPlaylists"))
        {
            String owner = (String) params[1];
            return getPlaylists(owner);
        }

        return null;
    }

    public static ArrayList<Playlist> getPlaylists(String owner) {
        OneStreamRestService restService = new OneStreamRestService();
        String sql = "SELECT * FROM Playlist WHERE OWNER = '"+ owner + "';";
        ArrayList<String[]> names = restService.getPlaylistNames(sql);



        ArrayList<Playlist> playlists = new ArrayList<Playlist>();
        ArrayList<String> queries = new ArrayList<String>();
        for (int i = 0; i < names.size(); i++) {
            sql = "SELECT * FROM " + names.get(i)[1] + ", Song WHERE " +
            "Song.uri = " + names.get(i)[1] + ".SongUri " +
            "ORDER BY " + names.get(i)[1] + ".ListPosition";
            Playlist p = restService.getPlaylist(sql);
            p.setName(names.get(i)[0]);
            p.setOwner(owner);
            playlists.add(p);
        }

        return playlists;
    }

    private static void createNewPlaylist(Playlist playlist) {

        OneStreamRestService restService = new OneStreamRestService();

        String tableName = playlist.getOwner() + "_" + playlist.getName().replaceAll(" ","");
        String sql = "CREATE TABLE "+ tableName +
                " (ListPosition int, SongName varchar(30), " +
                "SongUri varchar(30), PRIMARY KEY (SongUri));";
        restService.createPlaylist(sql);


        sql = "INSERT INTO Playlist (Name, Owner, SongsTable) values ('" +
                playlist.getName() + "', '" +  playlist.getOwner() + "', '" + tableName + "');";

        restService.addPlaylistToPlaylists(sql);
        ArrayList<Song> songs = playlist.getSongInfo();
        sql = "";


        ArrayList<String> queries = new ArrayList<String>();
        for (int i = 0; i < songs.size(); i++)
        {
            queries .add("INSERT INTO Song (uri, album, artist, type) " +
                    "values ('" + songs.get(i).getUri() + "', " +
                    "'" + songs.get(i).getAlbum() +"', " +
                    "'" + songs.get(i).getArtist() +"', " +
                    "'" + songs.get(i).getType() +"');");
        }


        if (queries.size() > 0) {
            restService.addSongsToPlaylists(queries);
        }
        return;
    }

}
