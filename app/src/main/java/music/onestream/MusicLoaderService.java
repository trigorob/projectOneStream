package music.onestream;

import android.os.AsyncTask;

import org.json.JSONArray;

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

public class MusicLoaderService extends AsyncTask {
    AsyncResponse SAR;

    @Override
    protected void onPostExecute(Object result) {
        SAR.processFinish((Playlist) result);
    }

    //This is the async process that gets songs. It only gets 50 right now.
    //Need to change this so it gets first 20 offset, then next 20 ater scrolled to bottom of list.
    @Override
    protected Playlist doInBackground(Object[] params) {
        ArrayList<Song> totalListContent = (ArrayList<Song>) params[0];
        Playlist listContent = (Playlist) params[1];
        int offset = (int) params[2];

        if (listContent.size() < totalListContent.size())
        {
            for (int i = offset; i < 20+offset; i++)
            {
                if (listContent.size() == totalListContent.size())
                {
                    return listContent;
                }
                listContent.addSong(totalListContent.get(i));
            }
        }

        return listContent;
    }
}
