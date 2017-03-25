package music.onestream.activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import music.onestream.util.Constants;
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
                              ImageButton loginButton, ImageButton fabIO, ImageButton prev,
                              ImageButton next, ImageButton rewind,
                              ImageButton random, SeekBar seekbar, ListView mainList) {

        playerHandler = PlayerActionsHandler.initPlayerActionsHandler(context, fabIO, prev, next,
                rewind, random, loginButton, mainList, seekbar, parentClass);
        initSpotifyPlayer();
        return playerHandler;
    }

    public void initSpotifyPlayer() {
        CredentialsHandler CH = new CredentialsHandler();
        final String accessToken = CH.getToken(getBaseContext(), "Spotify");

        SharedPreferences settings = getSharedPreferences(Constants.cacheSongsLoc, 0);
        Boolean songCachingOn = settings.getBoolean(Constants.cacheSongOn, false);
        playerHandler.initSpotifyPlayer(accessToken, songCachingOn);
    }

}
