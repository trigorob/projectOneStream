package music.onestream;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

    /**
     * Created by ruspe_000 on 2017-02-03.
     */

    public class SpotifyMusicLoader extends AsyncTask {
        AsyncResponse SAR;

        @Override
        protected void onPostExecute(Object result) {
            Object[] retObject = new Object[2];
            retObject[0] = "SpotifyMusicGetter";
            retObject[1] = result;
            SAR.processFinish(retObject);
        }

        //This is the async process that gets songs. It only gets 50 right now.
        //Need to change this so it gets first 20 offset, then next 20 ater scrolled to bottom of list.
        @Override
        protected ArrayList<Song> doInBackground(Object[] params) {
            ArrayList<Song> result = null;
            String token = (String) params[0];
            String offset = ((Integer) params[1]).toString();
            try {
                String urlString = "https://api.spotify.com/v1/me/tracks?offset=" + offset + "&limit=50";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Authorization", "Bearer " + token);
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
