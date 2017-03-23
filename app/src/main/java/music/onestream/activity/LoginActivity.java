package music.onestream.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

//Authentication tool for GoogleMusic
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;


import java.util.concurrent.TimeUnit;

import music.onestream.util.ColorCalculator;
import music.onestream.util.Constants;
import music.onestream.util.CredentialsHandler;
import music.onestream.R;
import music.onestream.util.Logger;
import music.onestream.util.LoginHandler;

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
            final AuthenticationRequest request = new AuthenticationRequest.Builder(Constants.SPOTIFY_ID,
                    AuthenticationResponse.Type.TOKEN, Constants.SPOTIFY_REDIRECT_URI)
                    .setScopes(new String[]{"user-library-read", "user-read-private", "playlist-read",
                            "playlist-read-private", "streaming"}).setShowDialog(true)
                    .build();
        AuthenticationClient.openLoginActivity(this, Constants.REQUEST_CODE, request);
    }

    public void onGoogleMusicLoginButtonClicked(View view) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(getGoogleApiClient());
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
    }

    public void googleMusicGetToken() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(getGoogleApiClient());
        startActivityForResult(signInIntent, Constants.RC_GET_TOKEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        String response = LoginHandler.handleLogin(requestCode, resultCode, intent, this.getApplicationContext());
        Logger logger = new Logger(this.getApplicationContext(), LoginActivity.class.getSimpleName());
        if (response.equals("Error"))
        {
            logger.logError("Login Failed");
        }
        else if (response.equals("GoogleGetToken"))
        {
            googleMusicGetToken();
        }
        else
        {
            logger.logMessage("Login Success!");
            startMainActivity(response, Constants.spotify);
        }
    }

}