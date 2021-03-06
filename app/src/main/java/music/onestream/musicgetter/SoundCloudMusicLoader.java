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

public class SoundCloudMusicLoader extends AsyncTask{
    public AsyncResponse SAR;

    @Override
    protected void onPostExecute(Object result) {
        Object[] retObject = new Object[2];
        retObject[0] = "SoundCloudMusicGetter";
        retObject[1] = result;
        SAR.processFinish(retObject);
    }
    @Override
    protected Object doInBackground(Object[] params) {
        String token = (String) params[0];
        String href = (String) params[1];

        try {
            String urlString = href;
            if (href == null || href.equals(Constants.defaultHref)) {
                 urlString = "https://api.soundcloud.com/me/favorites?" + "limit="
                        + Constants.soundCloudLoadStepSize + "&oauth_token=" + token
                        + "&linked_partitioning=1";
            }
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "application/json");
            //Get the songs as an object
            String json = (JSONExtractor.extractJSON(conn));
            if (json != null)
            {
                return JSONExtractor.processSoundCloudJSON(json);
            }
        } catch (IOException IOE) {}
        return null;
    }
}
