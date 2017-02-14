package music.onestream;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by ruspe_000 on 2017-02-03.
 */

public class PlaylistActivity extends Activity {

    public static ArrayList<String[]> combinedList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_activity);
        this.combinedList =  MainActivity.getCombinedList();
        combinedList.get(0)[0] = null;
    }
}
