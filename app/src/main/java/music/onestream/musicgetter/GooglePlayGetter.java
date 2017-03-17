package music.onestream.musicgetter;

/**
 * Created by ChampionRobert on 2017-02-16.
 */
/*
public class GooglePlayGetter extends AsyncTask {
    AsyncResponse SAR;

    @Override
    protected Object doInBackground(Object[] params) {
        ArrayList<Song> result = null;
        String token = (String) params[0];
        String offset = ((Integer) params[1]).toString();

        try {
            Process p = Runtime.getRuntime().exec("python GooglePythonGetter.py");
            int a = p.exitValue();
            URL url = new URL(new Integer(a).toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "application/json");
            //Get the songs as an object

            // (ALT)

            String url = new Integer(a).toString(); // your URL here
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.start();




            result = extractJSON(conn, (Integer) params[1]);

        } catch (IOException e) {
            e.printStackTrace();
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

    public ArrayList<Song> processJSON(String output, int gmusicSongOffset) {
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
                        artist, album, "Spotify", gmusicSongOffset + i);
                tempList.add(song);

            }
            return tempList;
        } catch (JSONException e) {
        }
        return null;
    }


    //
}
*/
