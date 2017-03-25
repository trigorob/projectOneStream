package music.onestream.activity;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import music.onestream.R;
import music.onestream.util.Constants;
import music.onestream.util.Logger;
import music.onestream.util.LoginHandler;

/**
 * Created by ruspe_000 on 2017-03-22.
 */

public class OSAuthenticationActivity extends OSActivity {

    private static LoginHandler loginHandler;

    public void startMainActivity() {
        Intent intent = OneStreamActivity.createIntent(this);
        startActivity(intent);
        finish();
    }

    public LoginHandler getLoginHandler() {
        if (loginHandler == null)
        {
            loginHandler = new LoginHandler(this);
        }
        else
        {
            loginHandler.setActivity(this);
        }
        return loginHandler;
    }

    public void onLoginActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        String response;
        Logger logger = new Logger(this.getApplicationContext(), LoginActivity.class.getSimpleName());
        if (getLoginHandler() != null && getLoginHandler().isAuthenticationIntentSoundCloud(intent)) {
            getLoginHandler().getSoundCloudToken(intent);
            logger.logMessage("Login Success!");
            startMainActivity();
        }
        else {
            response = getLoginHandler().handleLogin(requestCode, resultCode, intent);
            if (response.equals("Error")) {
                logger.logError("Login Failed");
            } else {
                logger.logMessage("Login Success!");
                startMainActivity();
            }
        }
    }

    public void handleLogin(int type) {
        if (type == Constants.OneStream_Spotify_Pos) {
            final AuthenticationRequest request = new AuthenticationRequest.Builder(Constants.SPOTIFY_ID,
                    AuthenticationResponse.Type.TOKEN, Constants.SPOTIFY_REDIRECT_URI)
                    .setScopes(new String[]{"user-library-read", "user-read-private", "playlist-read",
                            "playlist-read-private", "streaming"}).setShowDialog(true)
                    .build();
            AuthenticationClient.openLoginActivity(this, Constants.REQUEST_CODE, request);
        }
        else if (type == Constants.OneStream_SoundCloud_Pos)
        {
            getLoginHandler().authenticateSoundcloud();
        }
    }
}
