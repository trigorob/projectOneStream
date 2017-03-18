package music.onestream.musicgetter;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import music.onestream.song.Song;
import music.onestream.util.AsyncResponse;
import music.onestream.util.Constants;
import music.onestream.util.CredentialsHandler;
import music.onestream.util.JSONExtractor;

/**
 * Created by ChampionRobert on 2017-03-09.
 */

public class GoogleMusicLoader extends AsyncTask{
    public AsyncResponse SAR;

    @Override
    protected void onPostExecute(Object result) {
        Object[] retObject = new Object[2];
        retObject[0] = "SpotifyMusicGetter";
        retObject[1] = result;
        SAR.processFinish(retObject);
    }
    @Override
    protected Object doInBackground(Object[] params) {
        ArrayList<Song> result = null;
        // String token = (String) params[0];
        //String token = CredentialsHandler.getToken(context, "GoogleMusic");
        String offset = ((Integer) params[1]).toString();


        try {
            String urlString = "https://api.spotify.com/v1/me/tracks?offset=" + offset + "&limit=" +
                    Constants.googleLoadStepSize;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "application/json");
            //Get the songs as an object
            String json = (JSONExtractor.extractJSON(conn));
            if (json != null)
            {
                result = JSONExtractor.processSpotifyJSON(json, (int) params[1]);
            }
        } catch (IOException IOE) {

        }
        return result;
    }
}
