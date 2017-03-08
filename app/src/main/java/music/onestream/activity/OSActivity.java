package music.onestream.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;

import music.onestream.util.PlayerActionsHandler;
import music.onestream.util.CredentialsHandler;

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
                              Button loginButton, ImageButton fabIO, ImageButton prev,
                              ImageButton next, ImageButton rewind,
                              ImageButton random, SeekBar seekbar, ListView mainList) {

        playerHandler = new PlayerActionsHandler(context, fabIO, prev, next,
                rewind, random, loginButton, mainList, seekbar, parentClass);

        CredentialsHandler CH = new CredentialsHandler();
        final String accessToken = CH.getToken(getBaseContext(), "Spotify");
        playerHandler.initSpotifyPlayer(accessToken);
        return playerHandler;
    }
}
