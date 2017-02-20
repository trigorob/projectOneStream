package music.onestream;

import android.os.AsyncTask;
import java.sql.Connection;
import java.sql.DriverManager;
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
        SAR.processFinish((String) result);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        String action = (String) params[0];
        Playlist playlist = (Playlist) params[1];

        if (action.equals("CreatePlaylist"))
        {
            createNewPlaylist(playlist);
        }

        return null;
    }

    //Todo: need to create table @username, so that multiple playlists with same name can exist for different users
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
        String songValues = "";

        for (int i = 0; i < songs.size(); i++)
        {
            if (i > 0)
            {
                songValues = songValues + ",";
            }
            songValues = songValues +"(" + i + ", '" + songs.get(i).getName() + "', '"
                    + songs.get(i).getUri() +"')";
        }


        if (songValues.length() > 1) {
            sql = "INSERT INTO " + tableName + "  (ListPosition, SongName, SongUri) values " + songValues + ";";
            restService.addPlaylistToPlaylists(sql);
        }
        return;
    }

}
