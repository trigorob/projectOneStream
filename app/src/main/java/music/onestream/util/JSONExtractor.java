package music.onestream.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;

import music.onestream.playlist.Playlist;
import music.onestream.song.Song;

/**
 * Created by ruspe_000 on 2017-03-02.
 */

public class JSONExtractor {

    public static String extractJSON(HttpURLConnection url) {
        HttpURLConnection c = url;
        StringBuilder sb = new StringBuilder();
        try {
            c.connect();
            int status = c.getResponseCode();
            if (status == 200 || status == 201) {
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                String line;
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

    public static ArrayList<Song> processSpotifyJSON(String output) {
        try {
            JSONObject jsonObject = new JSONObject(output);
            JSONArray jArray = jsonObject.getJSONArray("items");
            String album = "";
            String artist = "";
            String name = "";
            String uri = "";
            String albumArt = null;
            String genre = Constants.defaultArtistsAlbumGenreName; //Until Spotify gives us genres, use default
            ArrayList<Song> tempList = new ArrayList<Song>();

            for (int i = 0; i < jArray.length(); i++) {
                try {
                    jsonObject = (JSONObject) new JSONObject(jArray.get(i).toString()).get("track");
                    name = (String) jsonObject.get("name");
                    uri = (String) jsonObject.get("uri");
                    artist = (String) jsonObject.getJSONArray("artists").getJSONObject(0).get("name");
                    jsonObject = jsonObject.getJSONObject("album");
                    album = (String) jsonObject.get("name");
                }
                catch (Exception e) {
                if (jsonObject == null)
                    artist = Constants.defaultArtistsAlbumGenreName;
                    album = Constants.defaultArtistsAlbumGenreName;
                }
            if (artist == null || artist.equals("")) {
                artist = Constants.defaultArtistsAlbumGenreName;
            }
            if (album == null || album.equals("")) {
                album = Constants.defaultArtistsAlbumGenreName;
            }
            Song song = new Song(name, uri, artist, album, "Spotify", 1, albumArt, genre);

            jsonObject = jsonObject.getJSONArray("images").getJSONObject(0);
            albumArt = (String) jsonObject.get("url");
            song.setAlbumArt(albumArt);
            tempList.add(song);

        }
        return tempList;
    } catch (JSONException e) {
    }
        return null;
}

    public static Object[] processSoundCloudJSON(String output) {
        Object[] retArr = new Object[2];
        String nextSongs = "";
        String artist = Constants.defaultArtistsAlbumGenreName;
        String album = Constants.defaultArtistsAlbumGenreName;
        ArrayList<Song> tempList = new ArrayList<Song>();
        try {
            JSONObject jsonObject= new JSONObject(output);
            if (jsonObject.length() > 1) {
                nextSongs = jsonObject.get("next_href").toString();
            }
            JSONArray jArray = (JSONArray) jsonObject.get("collection");
            for (int i = 0; i < jArray.length(); i++)
            {
                jsonObject = new JSONObject(jArray.get(i).toString());
                if ((Boolean) jsonObject.get("streamable")) {
                    String uri = (String) jsonObject.get("stream_url");
                    String name = (String) jsonObject.get("title");
                    String albumArt = "";
                    String genre = "";
                    try {
                        albumArt = (String) jsonObject.get("artwork_url");
                    }
                    catch (Exception e)
                    {
                        albumArt = null;
                    }
                    try {
                        genre = (String) jsonObject.get("genre");
                    }
                    catch (Exception e)
                    {
                        genre = Constants.defaultArtistsAlbumGenreName;
                    }
                    Song song = new Song(name, uri, artist, album, Constants.soundCloud, 1, albumArt, genre);
                    tempList.add(song);
                }

            }
        }
        catch (JSONException e)
        {

        }
        retArr[0] = nextSongs;
        retArr[1] = tempList;
        return retArr;
    }

    public static ArrayList<Playlist> processPlaylistJSON(String output) {
        if (output == null)
        {
            return new ArrayList<Playlist>();
        }
        ArrayList<Playlist> playlists = new ArrayList<Playlist>();
        try {
            Playlist playlist;
            JSONArray jArray = new JSONArray(output);
            JSONArray jArray2 = null;
            JSONObject jsonObject;
            for (int i = 0; i < jArray.length(); i++) {
                try {
                    if (jArray.get(i).getClass().equals(JSONArray.class))
                    {
                        jArray = new JSONArray(jArray.get(i).toString());
                        jsonObject = (JSONObject) jArray.get(0);
                    }
                    else {
                        jsonObject = new JSONObject(jArray.get(i).toString());
                    }
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
                            String albumArt = (String) jsonObject.get("albumArt");
                            String genre = (String) jsonObject.get("genre");
                            Song song = new Song(sName, uri, artist, album, type, 1, albumArt, genre);
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
