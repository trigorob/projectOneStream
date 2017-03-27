package music.onestream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import music.onestream.playlist.Playlist;
import music.onestream.song.Song;
import music.onestream.util.JSONExtractor;

/**
 * Created by ruspe_000 on 2017-03-12.
 */

public class JSONExtractorTest {

    private HttpURLConnection url;
    private Playlist playlist;
    private Playlist playlist2;
    private Song song;
    private Song song2;
    private Song song3;
    private ArrayList<Song> testSongs;
    private ArrayList<Playlist> testPlaylists;
    private String spotifyJSON =
            "[{ "
            + "\"added_at\" : \"2017-02-22T06:14:31Z\", "
            + "\"track\" : { "
            + "\"album\" : { "
            + "\"album_type\" : \"album\", "
            + "\"artists\" : [ { "
            + "\"external_urls\" : { "
            + "           \"spotify\" : \"https://open.spotify.com/artist/3Y10boYzeuFCJ4Qgp53w6o\" "
            + "       }, "
            + "       \"href\" : \"https://api.spotify.com/v1/artists/3Y10boYzeuFCJ4Qgp53w6o\", "
            + "             \"id\" : \"3Y10boYzeuFCJ4Qgp53w6o\", "
            + "             \"name\" : \"Scissor Sisters\", "
            + "             \"type\" : \"artist\", "
            + "             \"uri\" : \"spotify:artist:3Y10boYzeuFCJ4Qgp53w6o\" "
            + " } ], "
            + " \"available_markets\" : [ \"AD\", \"AR\", \"AT\", \"AU\", \"BE\", \"BG\", \"BO\", \"BR\", \"CH\", \"CL\", \"CO\", \"CR\", \"CY\", \"CZ\", \"DE\", \"DK\", \"DO\", \"EC\", \"EE\", \"ES\", \"FI\", \"FR\", \"GB\", \"GR\", \"GT\", \"HK\", \"HN\", \"HU\", \"ID\", \"IE\", \"IS\", \"IT\", \"LI\", \"LT\", \"LU\", \"LV\", \"MC\", \"MT\", \"MY\", \"NI\", \"NL\", \"NO\", \"NZ\", \"PA\", \"PE\", \"PH\", \"PL\", \"PT\", \"PY\", \"SE\", \"SG\", \"SK\", \"SV\", \"TR\", \"TW\" ], "
            + " \"external_urls\" : { "
            + "     \"spotify\" : \"https://open.spotify.com/album/2Uv5xoWfFbl2o7KdcCglOD\" "
            + " }, "
            + " \"href\" : \"https://api.spotify.com/v1/albums/2Uv5xoWfFbl2o7KdcCglOD\", "
            + "         \"id\" : \"2Uv5xoWfFbl2o7KdcCglOD\", "
            + "         \"images\" : [ { "
            + "     \"height\" : 640, "
            + "             \"url\" : \"https://i.scdn.co/image/bfb21f1026245457738ed73f535d079bf6450ada\", "
            + "             \"width\" : 640 "
            + " }, { "
            + "     \"height\" : 300, "
            + "             \"url\" : \"https://i.scdn.co/image/d5cdb01f53a0caaae7f71aaae216e5c737eeaaa4\", "
            + "             \"width\" : 300 "
            + " }, { "
            + "     \"height\" : 64, "
            + "             \"url\" : \"https://i.scdn.co/image/5f6d79f2e238a3075e977c4b108d0cb62852eb98\", "
            + "             \"width\" : 64 "
            + " } ], "
            + " \"name\" : \"Magic Hour\", "
            + "         \"type\" : \"album\", "
            + "         \"uri\" : \"spotify:album:2Uv5xoWfFbl2o7KdcCglOD\" "
            + "}, "
            + "\"artists\" : [ { "
            + "     \"external_urls\" : { "
            + "         \"spotify\" : \"https://open.spotify.com/artist/3Y10boYzeuFCJ4Qgp53w6o\" "
            + "     }, "
            + "     \"href\" : \"https://api.spotify.com/v1/artists/3Y10boYzeuFCJ4Qgp53w6o\", "
            + "             \"id\" : \"3Y10boYzeuFCJ4Qgp53w6o\", "
            + "             \"name\" : \"Scissor Sisters\", "
            + "             \"type\" : \"artist\", "
            + "             \"uri\" : \"spotify:artist:3Y10boYzeuFCJ4Qgp53w6o\" "
            + "    } ], "
            + " \"available_markets\" : [ \"AD\", \"AR\", \"AT\", \"AU\", \"BE\", \"BG\", \"BO\", \"BR\", \"CH\", \"CL\", \"CO\", \"CR\", \"CY\", \"CZ\", \"DE\", \"DK\", \"DO\", \"EC\", \"EE\", \"ES\", \"FI\", \"FR\", \"GB\", \"GR\", \"GT\", \"HK\", \"HN\", \"HU\", \"ID\", \"IE\", \"IS\", \"IT\", \"LI\", \"LT\", \"LU\", \"LV\", \"MC\", \"MT\", \"MY\", \"NI\", \"NL\", \"NO\", \"NZ\", \"PA\", \"PE\", \"PH\", \"PL\", \"PT\", \"PY\", \"SE\", \"SG\", \"SK\", \"SV\", \"TR\", \"TW\" ], "
            + " \"disc_number\" : 1, "
            + "         \"duration_ms\" : 192560, "
            + "         \"explicit\" : false, "
            + "         \"external_ids\" : { "
            + "     \"isrc\" : \"GBUM71202503\" "
            + " }, "
            + " \"external_urls\" : { "
            + "     \"spotify\" : \"https://open.spotify.com/track/2Yxa3k0CecfZ5HVWQyxNvy\" "
            + " }, "
            + " \"href\" : \"https://api.spotify.com/v1/tracks/2Yxa3k0CecfZ5HVWQyxNvy\", "
            + "         \"id\" : \"2Yxa3k0CecfZ5HVWQyxNvy\", "
            + "         \"name\" : \"Self Control\", "
            + "         \"popularity\" : 23, "
            + "         \"preview_url\" : \"https://p.scdn.co/mp3-preview/b49c0aa3279a6f046bae18376108e7441b0b4b5f?cid=0785a1e619c34d11b2f50cb717c27da0\", "
            + "         \"track_number\" : 9, "
            + "         \"type\" : \"track\", "
            + "         \"uri\" : \"spotify:track:2Yxa3k0CecfZ5HVWQyxNvy\" "
            + "} "
            + " }]";

    private String soundCloudJSON = "{\"collection\":[{\"kind\":\"track\",\"id\":231321623,\"created_at\":\"2015/11/03 06:14:45 +0000\"" +
            ",\"permalink\":\"feelgoodinc\",\"streamable\":true,\"embeddable_by\":\"all\",\"downloadable\":false,\"purchase_url\":null," +
            "\"label_id\":null,\"purchase_title\":null,\"genre\":\"gorillaz\",\"title\":\"Feel Good Inc.\"," +
            "\"label_name\":null,\"release\":null,\"track_type\":null,\"key_signature\":null,\"isrc\":null," +
            "\"video_url\":null,\"bpm\":null,\"release_year\":null,\"release_month\":null,\"release_day\":null,\"original_format\":\"flac\"," +
            "\"license\":\"all-rights-reserved\",\"uri\":\"https://api.soundcloud.com/tracks/231321623\"" +
            ",\"user\":{\"id\":184434848,\"kind\":\"user\",\"permalink\":\"everythinggorillazp2\",\"username\":\"Everything Gorillaz - P2\"" +
            ",\"last_modified\":\"2016/03/08 17:44:20 +0000\",\"uri\":\"https://api.soundcloud.com/users/184434848\"" +
            ",\"permalink_url\":\"http://soundcloud.com/everythinggorillazp2\"" +
            ",\"avatar_url\":\"https://i1.sndcdn.com/avatars-000185939326-fpfaz9-large.jpg\"},"+
            "\"user_playback_count\":1,\"user_favorite\":true" +
            ",\"artwork_url\":\"https://i1.sndcdn.com/artworks-000134866575-mk19ov-large.jpg\"" +
            ",\"stream_url\":\"https://api.soundcloud.com/tracks/231321623/stream\"}]}";

    @Test
    public void extractPlaylistJSONtest() throws Exception {
        initPlaylists();
        ArrayList<Playlist> lists = JSONExtractor.processPlaylistJSON(getPlaylistsJSON(testPlaylists));
        assert lists.size() == 2;
        Playlist testPlaylist = lists.get(0);
        assert testPlaylist.equals(testPlaylists.get(0));
        assert !testPlaylist.equals(testPlaylists.get(1));
        testPlaylist = lists.get(1);
        assert testPlaylist.equals(testPlaylists.get(1));
        assert !testPlaylist.equals(testPlaylists.get(0));
    }

    @Test
    public void extractSpotifyJSONtest() throws Exception {
        initPlaylists();
        ArrayList<Song> songLists = processSpotifyJSON(new JSONArray(spotifyJSON), 4);
        assert songLists.size() == 1;
        Song testSong = songLists.get(0);
        assert testSong.equals(songLists.get(0));
    }

    @Test
    public void extractSoundCloudJSONtest() throws Exception {
        initPlaylists();
        ArrayList<Song> songLists = (ArrayList<Song>) ((Object[]) JSONExtractor.processSoundCloudJSON(soundCloudJSON))[1];
        assert songLists.size() == 1;
        Song testSong = songLists.get(0);
        assert testSong.equals(songLists.get(1));
    }


    public void initPlaylists() {
        playlist = new Playlist();
        playlist.setName("TestList");
        playlist.setOwner("TestOwner");
        song = new Song("TestN", "TestU", "TestA", "TestAA", "Spot", 0, "AA");
        playlist.addSong(song);

        playlist2 = new Playlist();
        playlist2.setName("TestList2");
        playlist2.setOwner("TestOwner2");
        song2 = new Song("TestN2", "TestU2", "TestA2", "TestAA2", "Spot2", 1, "AA2");
        playlist2.addSong(song);
        testPlaylists = new ArrayList<Playlist>();
        testPlaylists.add(playlist);
        testPlaylists.add(playlist2);
        testSongs = new ArrayList<Song>();
        song = new Song("Self Control", "spotify:track:2Yxa3k0CecfZ5HVWQyxNvy", "Scissor Sisters",
                "Magic Hour", "Spotify", 4, "https://i.scdn.co/image/bfb21f1026245457738ed73f535d079bf6450ada");
        testSongs.add(song);
        song3 = new Song("demon days", "https://api.soundcloud.com/tracks/231321623/stream", "N/A",
                "gorillaz", "SoundCloud", 1, "https://i1.sndcdn.com/artworks-000134866575-mk19ov-large.jpg");
        testSongs.add(song);
    }

    public String getPlaylistsJSON(ArrayList list) {
        ObjectMapper mapper = new ObjectMapper();
        String playlistJSON = "";
        try {
            playlistJSON = mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return playlistJSON;
    }


    //Using this method VS one in JSON extractor because copying that JSON is a huge pain.
    //Only difference is the first two lines of code (turning string to JSONArray. Rest is unaltered.
    public static ArrayList<Song> processSpotifyJSON(JSONArray jArray, int spotifySongOffset) {
        try {
            JSONObject jsonObject = null;
            String album = "";
            String artist = "";
            String name = "";
            String uri = "";
            String albumArt = null;

            ArrayList<Song> tempList = new ArrayList<Song>();

            for (int i = 0; i < jArray.length(); i++) {
                try {
                    jsonObject = (JSONObject) new JSONObject(jArray.get(i).toString()).get("track");
                    name = (String) jsonObject.get("name");
                    uri = (String) jsonObject.get("uri");
                    artist = (String) jsonObject.getJSONArray("artists").getJSONObject(0).get("name");
                    jsonObject = jsonObject.getJSONObject("album");
                    album = (String) jsonObject.get("name");

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
                Song song = new Song(name, uri, artist, album, "Spotify", spotifySongOffset + i, albumArt);

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
}