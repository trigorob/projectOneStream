package music.onestream;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;

import java.util.List;

/**
 * Created by ruspe_000 on 2017-03-05.
 */

public class OSActivity extends AppCompatActivity {

    private static PlayerActionsHandler playerHandler;

    public static PlayerActionsHandler getPlayerHandler() {
        return playerHandler;
    }

    public static Context getContext() {
        return playerHandler.context;
    }

    public PlayerActionsHandler initPlayerHandler(Context context, String parentClass,
                                  Button loginButton, FloatingActionButton fabIO, FloatingActionButton prev,
                                  FloatingActionButton next, FloatingActionButton rewind,
                                  FloatingActionButton random, SeekBar seekbar, ListView mainList) {

        playerHandler = new PlayerActionsHandler(context, fabIO, prev, next,
                rewind, random, loginButton, mainList, seekbar, parentClass);

        CredentialsHandler CH = new CredentialsHandler();
        final String accessToken = CH.getToken(getBaseContext(), "Spotify");
        playerHandler.initSpotifyPlayer(accessToken);
        return playerHandler;
    }
}
