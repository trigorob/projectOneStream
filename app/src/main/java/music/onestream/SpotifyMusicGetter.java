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

    public class SpotifyMusicGetter extends AsyncTask {
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
                result = extractJSON(conn, (Integer) params[1]);
            } catch (IOException IOE) {

            }
            return result;
        }

        public ArrayList<Song> extractJSON(HttpURLConnection url, Integer offset) {
            HttpURLConnection c = url;
            ArrayList<Song> result = null;
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
                            sb.append(line + "\n");
                        }
                        br.close();
                        result = processJSON(sb.toString(), offset);
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
            return result;
        }

        public ArrayList<Song> processJSON(String output, int spotifySongOffset) {
            try {
                JSONObject jsonObject = new JSONObject(output);
                JSONArray jArray = jsonObject.getJSONArray("items");
                JSONArray artArray = null;
                JSONArray albumArray = null;
                String album = "";
                String artist = "";

                ArrayList<Song> tempList = new ArrayList<Song>();

                for (int i = 0; i < jArray.length(); i++) {
                    try {
                        jsonObject = (JSONObject) new JSONObject(jArray.get(i).toString()).get("track");
                        artist = (String) jsonObject.getJSONArray("artists").getJSONObject(0).get("name");
                        album = (String) jsonObject.getJSONObject("album").get("name");
                    } catch (JSONException je) {
                        if (jsonObject == null)
                            artist = "<Unknown>";
                        album = "<Unknown>";
                    } catch (NullPointerException NE) {
                        artist = "<Unknown>";
                        album = "<Unknown>";
                    }
                    if (artist == null || artist.equals("")) {
                        artist = "<Unknown>";
                    }
                    if (album == null || album.equals("")) {
                        album = "<Unknown>";
                    }
                    Song song = new Song((String) jsonObject.get("name"),
                            (String) jsonObject.get("uri"),
                            artist, album, "Spotify", spotifySongOffset + i);
                    tempList.add(song);

                }
                return tempList;
            } catch (JSONException e) {
            }
            return null;
        }
    }
