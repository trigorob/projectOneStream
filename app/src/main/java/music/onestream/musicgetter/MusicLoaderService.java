package music.onestream.musicgetter;

import android.os.AsyncTask;

import java.util.ArrayList;

import music.onestream.playlist.Playlist;
import music.onestream.song.Song;
import music.onestream.util.AsyncResponse;

/**
 * Created by ruspe_000 on 2017-02-03.
 */

public class MusicLoaderService extends AsyncTask {
    public AsyncResponse SAR;

    @Override
    protected void onPostExecute(Object result) {
        Object[] retObject = new Object[2];
        retObject[0] = "MusicLoaderService";
        retObject[1] = result;
        SAR.processFinish(retObject);
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
