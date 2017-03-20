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

public class LoginActivity extends FragmentActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String token = CredentialsHandler.getToken(this, Constants.googleMusic);

        final ImageButton spotifyLoginButton = (ImageButton) findViewById(R.id.spotifyLoginLauncherButton);

        //Todo: Actually get token/authentication and put it here
        token = CredentialsHandler.getToken(this, Constants.googleMusic);
        final ImageButton googleMusicLoginButton = (ImageButton) findViewById(R.id.googleMusicLoginLauncherButton);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(LoginActivity.this.getResources().getString(R.string.server_client_id))
                .requestEmail().build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).enableAutoManage(this,null)
                .build();

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
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
    }

    public void googleMusicGetToken() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, Constants.RC_GET_TOKEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == Constants.REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    logMessage("Login Success!");
                    SharedPreferences settings = getSharedPreferences(Constants.oneStreamDomainLoc, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(Constants.spotifyLoginChanged, true);
                    editor.commit();

                    CredentialsHandler.setToken(this, response.getAccessToken(),
                            response.getExpiresIn(), TimeUnit.SECONDS, Constants.spotify);
                    startMainActivity(response.getAccessToken());
                    break;

                // Auth flow returned an error
                case ERROR:
                    logError("Auth error: " + response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    logError("Auth result: " + response.getType());
            }
        }
        else if (requestCode == Constants.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            if (result.isSuccess()) {
                googleMusicGetToken();
                return;
            }
        }

        else if (requestCode == Constants.RC_GET_TOKEN) {
            SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            if (result.isSuccess()) {
                String idToken = result.getSignInAccount().getIdToken();
                SharedPreferences.Editor editor = settings.edit();
                CredentialsHandler.setToken(this, idToken,
                        9999999, TimeUnit.SECONDS, Constants.googleMusic);
                startMainActivity(idToken);
                //editor.put("GoogleACCT", acct);
                // Get account information
            }
                return;
        }
    }

    private void startMainActivity(String token) {
        Intent intent = OneStreamActivity.createIntent(this);
        intent.putExtra(Constants.spotifyToken, token);
        startActivity(intent);
        finish();
    }

    private void logError(String msg) {
        Toast.makeText(this, "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
    }

    private void logMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }

}