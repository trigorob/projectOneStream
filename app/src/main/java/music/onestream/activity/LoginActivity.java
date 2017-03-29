package music.onestream.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import music.onestream.R;
import music.onestream.util.Constants;
import music.onestream.util.CredentialsHandler;

import static java.util.concurrent.TimeUnit.*;

public class LoginActivity extends OSAuthenticationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ImageButton oneStreamDomainButton = (ImageButton) findViewById(R.id.oneStreamDomainLauncherButton);
        oneStreamDomainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent oneStreamDomain = new Intent(v.getContext(), OneStreamDomainActivity.class);
                startActivityForResult(oneStreamDomain, 0);
            }
        });
    }

    public void onSpotifyLoginButtonClicked(View view) {
        handleLogin(Constants.OneStream_Spotify_Pos);
    }

    public void onSoundCloudLoginButtonClicked(View view) {
        handleLogin(Constants.OneStream_SoundCloud_Pos);
    }

    public void logout() {
        CredentialsHandler.setToken(getContext(), null, 0,
                SECONDS, Constants.soundCloud);
        CredentialsHandler.setToken(getContext(), null, 0,
                SECONDS, Constants.spotify);
        getPlayerHandler().getSpotifyPlayer().logout();
        OneStreamActivity.getPlaylistHandler().removeRemoteSongsFromLibrary();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (intent != null && requestCode == Constants.REQUEST_CODE) {
            onLoginActivityResult(requestCode, resultCode, intent);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

}