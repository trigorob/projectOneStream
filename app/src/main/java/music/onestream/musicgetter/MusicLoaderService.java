package music.onestream.musicgetter;

import android.os.AsyncTask;

import java.util.ArrayList;

import music.onestream.playlist.Playlist;
import music.onestream.playlist.PlaylistHandler;
import music.onestream.song.Song;
import music.onestream.util.AsyncResponse;
import music.onestream.util.Constants;

/**
 * Created by ruspe_000 on 2017-02-03.
 */

public class MusicLoaderService extends AsyncTask {
    public AsyncResponse SAR;

    @Override
    protected void onPostExecute(Object result) {
        Object[] retObject = new Object[2];
        retObject[0] = Constants.musicLoaderService;
        retObject[1] = result;
        SAR.processFinish(retObject);
    }

    //This is the async process that gets songs. It gets 50/thread
    @Override
    protected Playlist doInBackground(Object[] params) {
        ArrayList<Song> totalListContent = (ArrayList<Song>) params[0];
        Playlist listContent = (Playlist) params[1];
        int offset = (int) params[2];
        Playlist combinedList = (Playlist) params[3];


        if (listContent.size() < totalListContent.size())
        {
            for (int i = offset; i < Constants.localLoadStepSize+offset; i++)
            {
                if (listContent.size() == totalListContent.size())
                {
                    return listContent;
                }
                listContent.addSong(totalListContent.get(i));
                combinedList.addSong(totalListContent.get(i));
            }
        }

        return listContent;
    }
}
