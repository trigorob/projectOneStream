package music.onestream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//Authentication tool for SoundCloud
import com.jlubecki.soundcloud.webapi.android.auth.AuthenticationCallback;
import com.jlubecki.soundcloud.webapi.android.auth.AuthenticationStrategy;
import com.jlubecki.soundcloud.webapi.android.auth.SoundCloudAuthenticator;

import com.jlubecki.soundcloud.webapi.android.auth.browser.BrowserSoundCloudAuthenticator;
import com.jlubecki.soundcloud.webapi.android.auth.chrometabs.ChromeTabsSoundCloudAuthenticator;
import com.jlubecki.soundcloud.webapi.android.auth.webview.WebViewSoundCloudAuthenticator;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;


import java.util.concurrent.TimeUnit;

public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @SuppressWarnings("SpellCheckingInspection")
    private static final String SPOTIFY_ID = "0785a1e619c34d11b2f50cb717c27da0";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String SOUNDCLOUD_ID = "asNLcGe4DAQ1YHSRKNyCo15sfFnXDbvS";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String SPOTIFY_REDIRECT_URI = "testschema://callback";
    private static final String SOUNDCLOUD_REDIRECT_URI = "http://onestream.local/dashboard/";

    private SoundCloudAuthenticator mAuthenticator;
    private AuthenticationStrategy strategy;
    private AuthenticationCallback callback;

    private static final int REQUEST_CODE = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callback = new AuthenticationCallback() {
            @Override
            public void onReadyToAuthenticate(SoundCloudAuthenticator authenticator) {
                mAuthenticator = authenticator;
            }
        };

        String token = CredentialsHandler.getToken(this, "Spotify");
        Button spotifyLoginButton = (Button) findViewById(R.id.spotifyLoginLauncherButton);
        if (token == null) {
            spotifyLoginButton.setText(R.string.spotify_login_button);
        } else {
            spotifyLoginButton.setText(R.string.spotify_logout_button);
        }

        token = CredentialsHandler.getToken(this, "SoundCloud");
        Button soundCloudLoginButton = (Button) findViewById(R.id.soundCloudLoginLauncherButton);
        if (token == null) {
            soundCloudLoginButton.setText(R.string.soundcloud_login_button);
        } else {
            soundCloudLoginButton.setText(R.string.soundcloud_logout_button);
        }
    }

    public void onSpotifyLoginButtonClicked(View view) {
            final AuthenticationRequest request = new AuthenticationRequest.Builder(SPOTIFY_ID, AuthenticationResponse.Type.TOKEN, SPOTIFY_REDIRECT_URI)
                    .setScopes(new String[]{"user-library-read", "user-read-private", "playlist-read", "playlist-read-private", "streaming"}).setShowDialog(true)
                    .build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    public void onSoundCloudLoginButtonClicked(View view) {
        WebViewSoundCloudAuthenticator webViewAuthenticator =
                new WebViewSoundCloudAuthenticator(SOUNDCLOUD_ID, SOUNDCLOUD_REDIRECT_URI, this, REQUEST_CODE);

        strategy = new AuthenticationStrategy.Builder(this)
                .addAuthenticator(webViewAuthenticator) // Finally tries this
                .setCheckNetwork(true) // Makes sure the internet is connected first.
                .build();

        strategy.beginAuthentication(callback);
        mAuthenticator.launchAuthenticationFlow();
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
                    logMessage("Got token: " + response.getAccessToken());
                    CredentialsHandler.setToken(this, response.getAccessToken(), response.getExpiresIn(), TimeUnit.SECONDS, "Spotify");
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
    }

    private void startMainActivity(String token) {
        Intent intent = MainActivity.createIntent(this);
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