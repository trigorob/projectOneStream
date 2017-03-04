package music.onestream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ruspe_000 on 2017-03-02.
 */

public class JSONExtractor {

    public static String extractJSON(HttpURLConnection url) {
        HttpURLConnection c = url;
        StringBuilder sb = null;
        try {
            c.connect();
            int status = c.getResponseCode();
            if (status == 200 || status == 201) {
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                String line;
                sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
            }

        }
        catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return sb.toString();
        }
    }

    public static ArrayList<Song> processSpotifyJSON(String output, int spotifySongOffset) {
        try {
            JSONObject jsonObject = new JSONObject(output);
            JSONArray jArray = jsonObject.getJSONArray("items");
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

    public static ArrayList<Playlist> processPlaylistJSON(String output) {
        ArrayList<Playlist> playlists = new ArrayList<Playlist>();
        try {
            Playlist playlist;
            JSONArray jArray = new JSONArray(output);
            JSONArray jArray2 = null;
            JSONObject jsonObject;
            for (int i = 0; i < jArray.length(); i++) {
                try {
                    jsonObject = new JSONObject(jArray.get(i).toString());
                    playlist = new Playlist();
                    playlist.setName((String) jsonObject.get("name"));
                    playlist.setOwner((String) jsonObject.get("owner"));
                    Object songInfo = jsonObject.get("songInfo");
                    if (songInfo != null) {
                        jArray2 = new JSONArray(songInfo.toString());
                        for (int d = 0; d < jArray2.length(); d++) {
                            jsonObject = jArray2.getJSONObject(d);
                            String sName = (String) jsonObject.get("name");
                            String uri = (String) jsonObject.get("uri");
                            String artist = (String) jsonObject.get("artist");
                            String album = (String) jsonObject.get("album");
                            String type = (String) jsonObject.get("type");
                            Song song = new Song(sName, uri, artist, album, type, i);
                            playlist.addSong(song);
                        }
                    }
                    playlists.add(playlist);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return playlists;
    }
}
