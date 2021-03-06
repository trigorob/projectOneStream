package music.onestream.musicgetter;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import music.onestream.activity.OneStreamActivity;
import music.onestream.playlist.PlaylistHandler;
import music.onestream.song.Song;
import music.onestream.util.AsyncResponse;
import music.onestream.util.Constants;
import music.onestream.util.JSONExtractor;

/**
     * Created by ruspe_000 on 2017-02-03.
     */

    public class SpotifyMusicLoader extends AsyncTask {
        public AsyncResponse SAR;

        @Override
        protected void onPostExecute(Object result) {
            Object[] retObject = new Object[2];
            retObject[0] = Constants.spotifyMusicGetter;
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
                String urlString = "https://api.spotify.com/v1/me/tracks?offset=" + offset + "&limit="
                        + Constants.spotifyLoadStepSize;
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("accept", "application/json");
                //Get the songs as an object
                String json = (JSONExtractor.extractJSON(conn));
                if (json != null)
                {
                    result = JSONExtractor.processSpotifyJSON(json);
                }
            } catch (IOException IOE) {

            }
            return result;
        }
    }
