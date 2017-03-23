package music.onestream.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.concurrent.TimeUnit;

/**
 * Created by ruspe_000 on 2017-03-22.
 */

public class LoginHandler {

    public static String handleLogin(int requestCode, int resultCode, Intent intent, Context context) {
        // Check if result comes from the correct activity
        if (requestCode == Constants.REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    SharedPreferences settings = context.getSharedPreferences(Constants.oneStreamDomainLoc, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(Constants.spotifyLoginChanged, true);
                    editor.commit();

                    CredentialsHandler.setToken(context, response.getAccessToken(),
                            response.getExpiresIn(), TimeUnit.SECONDS, Constants.spotify);
                    return "Token:" +response.getAccessToken();

                // Auth flow returned an error
                case ERROR:
                    return "Login Failed";

                // Most likely auth flow was cancelled
                default:
                    return (response.getType().toString());
            }
        } else if (requestCode == Constants.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            if (result.isSuccess()) {
                return "GoogleGetToken";
            }
        } else if (requestCode == Constants.RC_GET_TOKEN) {
            SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            if (result.isSuccess()) {
                String idToken = result.getSignInAccount().getIdToken();
                SharedPreferences.Editor editor = settings.edit();
                CredentialsHandler.setToken(context, idToken,
                        9999999, TimeUnit.SECONDS, Constants.googleMusic);
                return "Token:" + idToken;
                //editor.put("GoogleACCT", acct);
                // Get account information
            }
        }
        return "";
    }
}
