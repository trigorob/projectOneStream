package music.onestream.util;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import com.jlubecki.soundcloud.webapi.android.auth.AuthenticationCallback;
import com.jlubecki.soundcloud.webapi.android.auth.AuthenticationStrategy;
import com.jlubecki.soundcloud.webapi.android.auth.SoundCloudAuthenticator;
import com.jlubecki.soundcloud.webapi.android.auth.webview.WebViewSoundCloudAuthenticator;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import java.util.concurrent.TimeUnit;

/**
 * Created by ruspe_000 on 2017-03-22.
 */

public class LoginHandler {

    private SoundCloudAuthenticator mAuthenticator;
    private AuthenticationStrategy strategy;
    private AuthenticationCallback soundcloudCallback;
    private Activity mActivity;
    private String authResult;

    public LoginHandler(Activity activity) {
        mActivity = activity;
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    public void setupSoundCloud() {
        soundcloudCallback = new AuthenticationCallback() {
            @Override
            public void onReadyToAuthenticate(SoundCloudAuthenticator authenticator) {
                mAuthenticator = authenticator;
            }
        };

        WebViewSoundCloudAuthenticator webViewAuthenticator =
                new WebViewSoundCloudAuthenticator
                        (Constants.SOUNDCLOUD_CLIENT_ID, Constants.SOUNDCLOUD_REDIRECT_URI,
                                mActivity, Constants.REQUEST_CODE);

        strategy = new AuthenticationStrategy.Builder(mActivity)
                .addAuthenticator(webViewAuthenticator)
                .setCheckNetwork(true)
                .build();

        strategy.beginAuthentication(soundcloudCallback);

    }

    public void authenticateSoundcloud() {
        setupSoundCloud();
        mAuthenticator.launchAuthenticationFlow();
    }

    public Boolean isAuthenticationIntentSoundCloud(Intent intent) {
        return ((intent != null && intent.getData() != null)
                && (intent.getData().getScheme() + "://" + intent.getData().getAuthority()
                + intent.getData().getPath()).equals(Constants.SOUNDCLOUD_REDIRECT_URI));
    }

    public void getSoundCloudToken(Intent intent) {
        if (strategy != null && strategy.canAuthenticate(intent)) {
            strategy.getToken(intent, Constants.SOUNDCLOUD_CLIENT_SECRET, new AuthenticationStrategy.ResponseCallback() {
                @Override
                public void onAuthenticationResponse(com.jlubecki.soundcloud.webapi.android.auth.models.AuthenticationResponse response) {
                    switch (response.getType()) {
                        case TOKEN:
                            storeSoundCloudToken(mActivity, response);
                            break;
                    }
                }

                @Override
                public void onAuthenticationFailed(Throwable throwable) {
                }
            });
        }
    }

    private String storeSpotifyToken(Activity mActivity, AuthenticationResponse response) {
        SharedPreferences settings = mActivity.getApplicationContext()
                .getSharedPreferences(Constants.oneStreamDomainLoc, 0);
        SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(Constants.spotifyLoginChanged, true);
        editor.commit();

        CredentialsHandler.setToken(mActivity.getApplicationContext(), response.getAccessToken(),
                response.getExpiresIn(), TimeUnit.SECONDS, Constants.spotify);
        authResult = "Token:" +response.getAccessToken();
        return authResult;
    }

    private String storeSoundCloudToken(Activity mActivity,
                com.jlubecki.soundcloud.webapi.android.auth.models.AuthenticationResponse response){
        SharedPreferences settings = mActivity.getApplicationContext()
                .getSharedPreferences(Constants.oneStreamDomainLoc, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(Constants.soundCloudLoginChanged, true);
        editor.commit();

        CredentialsHandler.setToken(mActivity.getApplicationContext(), response.access_token,
                999999999, TimeUnit.SECONDS, Constants.soundCloud);
        authResult = "Token:" +response.access_token;
        return authResult;
    }

    public String handleLogin(int requestCode, int resultCode, Intent intent) {
        if (requestCode == Constants.REQUEST_CODE) {
            // Check if result comes from the correct activity
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    storeSpotifyToken(mActivity, response);

                // Auth flow returned an error
                case ERROR:
                    authResult = "Login Failed";
                    return authResult;

                // Most likely auth flow was cancelled
                default:
                    authResult = (response.getType().toString());
                    return authResult;
            }
        }
        return "";
    }
}
