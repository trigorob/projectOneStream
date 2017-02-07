package music.onestream;

import android.os.AsyncTask;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ruspe_000 on 2017-02-03.
 */

public class SpotifyMusicGetter extends AsyncTask {
    AsyncResponse SAR;

    @Override
    protected void onPostExecute(Object result) {
        SAR.processFinish((String)result);
    }

    //This is the async process that gets songs. It only gets 50 right now.
    //Need to change this so it gets first 20 offset, then next 20 ater scrolled to bottom of list.
    @Override
    protected String doInBackground(Object[] params) {
        String result = null;
        String token = (String) params[0];
        try {
            String urlString = "https://api.spotify.com/v1/me/tracks";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "application/json");
            //Get the songs as an object
            result = getJSON(conn);
        } catch (IOException IOE) {

        }
        return result;
    }

    public String getJSON(HttpURLConnection url) {
        HttpURLConnection c = url;
        try {
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

}
