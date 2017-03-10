package music.onestream.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import music.onestream.util.CredentialsHandler;
import music.onestream.R;

public class LoginActivity extends FragmentActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @SuppressWarnings("SpellCheckingInspection")
    private static final String SPOTIFY_ID = "0785a1e619c34d11b2f50cb717c27da0";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String SPOTIFY_REDIRECT_URI = "testschema://callback";
    private static final int REQUEST_CODE = 1337;
    private static final int GOOGLE_RESPONSE_CODE = 0;
    private static final int GOOGLE_CACHE_CODE = -1;
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_GET_TOKEN = 9002;
    public static final String PREFS_NAME = "GoogleACCT";

    private static GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String token = CredentialsHandler.getToken(this, "GoogleMusic");

        Button spotifyLoginButton = (Button) findViewById(R.id.spotifyLoginLauncherButton);
        if (token == null) {
            spotifyLoginButton.setText(R.string.spotify_login_button);
        } else {
            spotifyLoginButton.setText(R.string.spotify_logout_button);
        }

        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(v.getContext(), SettingsActivity.class);
                startActivityForResult(back, 0);
            }
        });

        //Todo: Actually get token/authentication and put it here
        token = CredentialsHandler.getToken(this, "GoogleMusic");
        Button googleMusicLoginButton = (Button) findViewById(R.id.googleMusicLoginLauncherButton);
        if (token == null) {
            googleMusicLoginButton.setText(R.string.googlemusic_login_button);
        } else {
            googleMusicLoginButton.setText(R.string.googlemusic_logout_button);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(LoginActivity.this.getResources().getString(R.string.server_client_id))
                .requestEmail().build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).enableAutoManage(this,null)
                .build();

        Button oneStreamDomainButton = (Button) findViewById(R.id.oneStreamDomainLauncherButton);
        oneStreamDomainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent oneStreamDomain = new Intent(v.getContext(), OneStreamDomainActivity.class);
                startActivityForResult(oneStreamDomain, 0);
            }
        });
    }

    public void onSpotifyLoginButtonClicked(View view) {
            final AuthenticationRequest request = new AuthenticationRequest.Builder(SPOTIFY_ID, AuthenticationResponse.Type.TOKEN, SPOTIFY_REDIRECT_URI)
                    .setScopes(new String[]{"user-library-read", "user-read-private", "playlist-read", "playlist-read-private", "streaming"}).setShowDialog(true)
                    .build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    public void onGoogleMusicLoginButtonClicked(View view) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void googleMusicGetToken() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    logMessage("Login Success!");
                    CredentialsHandler.setToken(this, response.getAccessToken(),
                            response.getExpiresIn(), TimeUnit.SECONDS, "Spotify");
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
        else if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            if (result.isSuccess()) {
                googleMusicGetToken();
                return;
            }
        }

        else if (requestCode == RC_GET_TOKEN) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            if (result.isSuccess()) {
                String idToken = result.getSignInAccount().getIdToken();
                SharedPreferences.Editor editor = settings.edit();
                CredentialsHandler.setToken(this, idToken,
                        9999999, TimeUnit.SECONDS, "GoogleMusic");
                startMainActivity(idToken);
                //editor.put("GoogleACCT", acct);
                // Get account information
            }
                return;
        }
    }

    private void startMainActivity(String token) {
        Intent intent = OneStreamActivity.createIntent(this);
        intent.putExtra("SPOTIFY_TOKEN", token);
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