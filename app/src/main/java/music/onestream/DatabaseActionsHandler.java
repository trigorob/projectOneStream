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
        Object[] retObject = new Object[2];
        retObject[0] = "DatabaseActionsHandler";
        retObject[1] = result;
        SAR.processFinish(retObject);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        String action = (String) params[0];

        if (action.equals("GetPlaylists"))
        {
            String owner = (String) params[1];
            return getPlaylists(owner);
        }
        else if (action.equals("CreatePlaylist"))
        {
            Playlist playlist = (Playlist) params[1];
            createPlaylist(playlist);
        }
        else if (action.equals("DeletePlaylist"))
        {
            Playlist playlist = (Playlist) params[1];
            deletePlaylist(playlist);
        }
        //We do this with a delete/create rather than an update mostly for batch loading/speed reasons
        else if (action.equals("UpdatePlaylist"))
        {
            Playlist playlist = (Playlist) params[1];
            String newName = (String) params[2];
            String oldName = (String) params[3];
            playlist.setName(oldName); //Delete old table
            deletePlaylist(playlist);
            playlist.setName(newName); //Create new table
            createPlaylist(playlist);

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

    private static void createPlaylist(Playlist playlist) {

        OneStreamRestService restService = new OneStreamRestService();

        String tableName = playlist.getOwner() + "_" + playlist.getName().replaceAll(" ","");
        String sql = "CREATE TABLE "+ tableName +
                " (ListPosition int, SongName varchar(30), " +
                "SongUri varchar(50), PRIMARY KEY (SongUri));";
        restService.performUpdateQuery(sql);


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


        queries = new ArrayList<String>();
        for (int i = 0; i < songs.size(); i++)
        {
            queries .add("INSERT INTO " + tableName + " (ListPosition, SongName, SongUri) " +
                    "values ('" + i + "', " +
                    "'" + songs.get(i).getName() +"', " +
                    "'" + songs.get(i).getUri() +"');");
        }
        restService.addSongsToPlaylists(queries);
    }

    private static void deletePlaylist(Playlist playlist) {

        OneStreamRestService restService = new OneStreamRestService();

        String tableName = playlist.getOwner() + "_" + playlist.getName().replaceAll(" ","");
        String sql = "DROP TABLE "+ tableName + ";";
        restService.performUpdateQuery(sql);

        sql = "DELETE FROM Playlist WHERE Owner = '" + playlist.getOwner() + "' " +
                "AND SongsTable = '" + tableName + "';";

        restService.performUpdateQuery(sql);
    }

}
