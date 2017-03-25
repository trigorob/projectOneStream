package music.onestream.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import music.onestream.util.Constants;
import music.onestream.R;
import music.onestream.util.Logger;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (intent != null && requestCode == Constants.REQUEST_CODE) {
            onLoginActivityResult(requestCode, resultCode, intent);
        }
    }

}